package br.ufu.facom.network.dts.topology;

public class Link {
	private double usage;
	private double coast;
	
	Element peerA;
	Element peerB;
	
	public double getUsage() {
		return usage;
	}
	public void setUsage(double usage) {
		this.usage = usage;
	}
	public double getCoast() {
		return coast;
	}
	public void setCoast(double coast) {
		this.coast = coast;
	}
	public Element getPeerA() {
		return peerA;
	}
	public void setPeerA(Element peerA) {
		this.peerA = peerA;
	}
	public Element getPeerB() {
		return peerB;
	}
	public void setPeerB(Element peerB) {
		this.peerB = peerB;
	}
	
	public Element getPeer(Element source){
		if(peerA != null && peerB != null){
			if(peerA.equals(source))
				return peerB;
			else if(peerB.equals(source))
				return peerA;
		}
		
		return null;
	}
}
