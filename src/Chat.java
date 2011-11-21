import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.Utilities;

import br.ufu.facom.network.dlontology.FinSocket;
import br.ufu.facom.network.dlontology.msg.Message;

public class Chat extends JFrame{
	private JEditorPane viewPane;
	private JEditorPane senderPane;
	private JLabel header;
	private JPanel bar;
	private JComboBox destin;
	private StringBuffer buffer = new StringBuffer();
	private ButtonGroup wsGroup;
	private FinSocket fin;
	
	public Chat(){
		Object arg = JOptionPane.showInputDialog(Chat.this,"Com qual título você deseja se registrar?","Login",JOptionPane.INFORMATION_MESSAGE,new ImageIcon(Chat.class.getResource("logo.png")),null,null);
		if(arg == null || arg.toString().trim().isEmpty())
			System.exit(0);
		else{
			String title = arg.toString();
		
		
			createSenderPane();
			createBar(title);
			createOtherComponents();
		
			createLayout();
		
			createSocketAndListeners(title);
		
			this.setVisible(true);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setBounds(100, 100, 800, 600);
		
		}
	}

	private void createBar(String title) {
		//Panel 1
		JPanel panelTitle = new JPanel();
		BoxLayout layoutPan1 = new BoxLayout(panelTitle, BoxLayout.Y_AXIS);
		panelTitle.setLayout(layoutPan1);
		panelTitle.setBorder(BorderFactory.createTitledBorder("Título"));
		
		//Panel 2
		final JPanel panelWorkspaces = new JPanel();
		BoxLayout layoutPan2 = new BoxLayout(panelWorkspaces, BoxLayout.Y_AXIS);
		panelWorkspaces.setLayout(layoutPan2);
		panelWorkspaces.setBorder(BorderFactory.createTitledBorder("Workspaces"));
		
		//Panel 3
		JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		JLabel sourceLab = new JLabel(title,JLabel.CENTER);
		sourceLab.setFont(new Font("Verdana", Font.BOLD, 12));
		sourceLab.setForeground(Color.RED);
		panelTitle.add(sourceLab);
		
		wsGroup = new ButtonGroup();
		
		final JRadioButton padrao = new JRadioButton("Default",true);
		padrao.setFont(new Font("Verdana",Font.BOLD,10));
		wsGroup.add(padrao);
		panelWorkspaces.add(padrao);
		
		JButton join = new JButton("Join");
		JButton disjoin = new JButton("Disjoin");
		join.setIcon(new ImageIcon(Chat.class.getResource("mais.png")));
		disjoin.setIcon(new ImageIcon(Chat.class.getResource("menos.png")));
		panelButtons.add(disjoin);
		panelButtons.add(join);
		
		join.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String workspace = JOptionPane.showInputDialog(Chat.this,"Digite o nome do workspace:");
				if(workspace != null && !workspace.isEmpty()){
					if(fin.join(workspace)){
						JRadioButton novoWS = new JRadioButton(workspace);
						novoWS.setFont(new Font("Verdana",Font.BOLD,10));
						panelWorkspaces.add(novoWS);
						wsGroup.add(novoWS);
						panelWorkspaces.paintAll(panelWorkspaces.getGraphics());
					}else
						JOptionPane.showMessageDialog(Chat.this, "Não foi possível se conectar ao workspace selecionado.","Disjoin",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		disjoin.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(padrao.isSelected())
					JOptionPane.showMessageDialog(Chat.this, "Você não pode sair do workspace padrão do Chat!", "Disjoin", JOptionPane.ERROR_MESSAGE);
				else{
					for (Enumeration en = wsGroup.getElements(); en.hasMoreElements();) {
						JRadioButton b = (JRadioButton) en.nextElement();
						if (b.getModel() == wsGroup.getSelection()){
							
							if(fin.disjoin(b.getText())){
								panelWorkspaces.remove(b);
								wsGroup.remove(b);
								panelWorkspaces.paintAll(panelWorkspaces.getGraphics());
							}else
								JOptionPane.showMessageDialog(Chat.this, "Não foi possível se desconectar do workspace selecionado.","Disjoin",JOptionPane.ERROR_MESSAGE);
						}
					}
					
				}
			}
		});
		
		bar = new JPanel(new BorderLayout());
		bar.add(panelTitle,BorderLayout.NORTH);
		bar.add(panelWorkspaces,BorderLayout.CENTER);
		bar.add(panelButtons,BorderLayout.SOUTH);
	}

	private void createLayout() {
		JPanel south = new JPanel(new BorderLayout());
		south.add(new JScrollPane(senderPane),BorderLayout.CENTER);

		header = new JLabel(new ImageIcon(Chat.class.getResource("logo.png")),JLabel.CENTER);
		
		JPanel mainPan = new JPanel(new BorderLayout(10,10));
		mainPan.add(bar,BorderLayout.EAST);
		mainPan.add(new JScrollPane(viewPane),BorderLayout.CENTER);
		mainPan.add(header,BorderLayout.NORTH);
		mainPan.add(senderPane,BorderLayout.SOUTH);
		
		mainPan.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.setContentPane(mainPan);
	}

	private void createSenderPane() {
		senderPane = new JEditorPane();
		
		senderPane.setPreferredSize(new Dimension(100, 100));
		
		senderPane.setEditorKit(new StyledEditorKit());
		
		senderPane.setFont(new Font("Verdana",Font.BOLD|Font.ITALIC,20));
		
		senderPane.getDocument().addDocumentListener(new DocumentListener(){
            @Override
			public void insertUpdate(DocumentEvent event) {
                final DocumentEvent e=event;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
					public void run() {
                        if (e.getDocument() instanceof StyledDocument) {
                            try {
                                StyledDocument doc=(StyledDocument)e.getDocument();
                                int start= Utilities.getRowStart(senderPane,Math.max(0,e.getOffset()-1));
                                int end=Utilities.getWordStart(senderPane,e.getOffset()+e.getLength());
                                String text=doc.getText(start, end-start);
 
                                int i=text.indexOf(":)");
                                while(i>=0) {
                                    final SimpleAttributeSet attrs=new SimpleAttributeSet(
                                       doc.getCharacterElement(start+i).getAttributes());
                                    if (StyleConstants.getIcon(attrs)==null) {
                                        StyleConstants.setIcon(attrs, Smiles.SMILE_IMG);
                                        doc.remove(start+i, 2);
                                        doc.insertString(start+i,":)", attrs);
                                    }
                                    i=text.indexOf(":)", i+2);
                                }
                            } catch (BadLocationException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
            }
            @Override
			public void removeUpdate(DocumentEvent e) {
            }
            @Override
			public void changedUpdate(DocumentEvent e) {
            }
        });
		
		senderPane.requestFocusInWindow();
		
		senderPane.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				
			}
			@Override
			public void keyReleased(KeyEvent e) {
				if  (e.getKeyCode() == KeyEvent.VK_ENTER)
					if(e.getModifiers() == 0) {
						String msg = senderPane.getText();
					
						String destin = findWSDestin();
						
						if(fin.write(destin, msg))
							buffer.append("<b style=color:red>Você</b><b> para </b><b style=color:green>"+destin+"</b><b>: "+msg.replace("\n", "<br>")+"</b><br>");
						else
							buffer.append("<html><b style=color:red> Erro ao enviar mensagem! </b></br><br>");
						
							
						viewPane.setText("<html>"+buffer+"</html>");
						
						
						senderPane.setText("");
					} else
						try {
							senderPane.getDocument().insertString(senderPane.getDocument().getLength(), "\n", null);
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	private void createSocketAndListeners(final String source) {
		fin = FinSocket.open();
		
		if(!fin.register(source)){
        	JOptionPane.showMessageDialog(Chat.this, "Não foi possível se registrar no DTS com o título \""+source+"\"");
	       	System.exit(1);
	    }
		
		if(!fin.join("Default")){
        	JOptionPane.showMessageDialog(Chat.this, "Não foi possível \"entrar\" no workspace Default do Chat");
	       	System.exit(1);
	    }
		
		this.addWindowListener( new WindowAdapter(){   
			@Override
			public void windowClosing(WindowEvent evt){
				if(JOptionPane.showConfirmDialog(Chat.this, "Deseja realmente sair?", "",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
					fin.close();
					System.exit(0);
				}else
					Chat.this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			}  
		});
		
		new Thread(){
			@Override
			public void run(){
				while(true){
					Message msg = fin.read();
					if(msg != null || !msg.getPayload().isEmpty()){
							buffer.append("<b style=color:blue>"+msg.getSource()+"</b><b> para </b><b style=color:green>"+msg.getDestination()+"</b><b>: "+msg.getPayload().replace("\n", "<br>")+"</b><br>");						
							
						viewPane.setText("<html>"+buffer+"</html>");
						
					}
				}
			}
		}.start();
	}

	protected String findWSDestin() {
		for (Enumeration en = wsGroup.getElements(); en.hasMoreElements();) {
			JRadioButton b = (JRadioButton) en.nextElement();
			if (b.getModel() == wsGroup.getSelection()){
				return b.getText();
			}
		}
		return "";
	}

	private void createOtherComponents() {
		
		viewPane = new  JEditorPane();
		viewPane.setContentType("text/html");
		viewPane.setEditable(false);
		
		viewPane.getDocument().addDocumentListener(new DocumentListener(){
            @Override
			public void insertUpdate(DocumentEvent event) {
                final DocumentEvent e=event;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
					public void run() {
                        if (e.getDocument() instanceof StyledDocument) {
                            try {
                                StyledDocument doc=(StyledDocument)e.getDocument();
                                int start= Utilities.getRowStart(viewPane,Math.max(0,e.getOffset()-1));
                                int end=Utilities.getWordStart(viewPane,e.getOffset()+e.getLength());
                                String text=doc.getText(start, end-start);
 
                                int i=text.indexOf(":)");
                                while(i>=0) {
                                    final SimpleAttributeSet attrs=new SimpleAttributeSet(
                                       doc.getCharacterElement(start+i).getAttributes());
                                    if (StyleConstants.getIcon(attrs)==null) {
                                        StyleConstants.setIcon(attrs, Smiles.SMILE_IMG);
                                        doc.remove(start+i, 2);
                                        doc.insertString(start+i,":)", attrs);
                                    }
                                    i=text.indexOf(":)", i+2);
                                }
                            } catch (BadLocationException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
            }
            @Override
			public void removeUpdate(DocumentEvent e) {
            }
            @Override
			public void changedUpdate(DocumentEvent e) {
            }
        });
	}

	public static void main(String[] args) {
		new Chat();
	}

}