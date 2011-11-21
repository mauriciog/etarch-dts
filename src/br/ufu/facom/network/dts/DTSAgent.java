package br.ufu.facom.network.dts;

import java.util.List;

import br.ufu.facom.network.dlontology.FinSocket;
import br.ufu.facom.network.dlontology.msg.Message;
import br.ufu.facom.network.dts.bean.Entity;
import br.ufu.facom.network.dts.bean.Workspace;

public class DTSAgent {
	private FinSocket fin;
	
	private List<Entity> entities;
	private List<Workspace> workspaces;
	
	public void run(){
		fin = FinSocket.open();
		
		if(fin.register("DTS - UFU POC")) { //Register a new DTS (any other DTS can deny this request)
			if(fin.join("DTS")){ //JOIN in the DTS workspace. If it doesn't exists, just create the workspace as owner
				while(true){
					Message msg = fin.read();
					if(msg != null || !msg.getPayload().isEmpty()){
						/*
						 * TODO Create a parser
						 */
						
						/*
						 * if subscriber
						 * Verify the regulatory organization (get the requirements too)
						 * Add to the list of entities 
						 * Anwswer the subscriber request
						 */
						
						
						/*
						 * if unsubscriber
						 * Remove from all workpsaces (Notify the owners)
						 * Remove from list of entities
						 * Anwswer the subscriber request
						 */
						
						/*
						 * if join
						 * Verify if the entity are registered
						 * Verify if the workspace are created, if not, create a new workspace as owner
						 *   - if so, verify if workspace is public
						 *        - if so, add the entity to the workspace
						 *    - if not, ask to owner about the join of the entity
						 *        - if it agree, add the entity to the workspace
						 * Answer the join request 
						 * Anwswer the subscriber request
						 */
						
						/*
						 * if disjoin
						 * Verify if the entity are registered
						 * Verify if the entity belongs to workspace
						 * Remove from workspace 
						 * Anwswer the subscriber request
						 */
						
						/*
						 * if removeRequirement
						 * (...)
						 */
						
						
						
					}
				}
			}
		}
	}
}
