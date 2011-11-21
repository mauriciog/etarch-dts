package br.ufu.facom.network.dts.bean;

import java.util.List;

public class Workspace extends Entity{
	private List<Entity> members;
	private Entity owner;
	private boolean Public;
	
	public List<Entity> getMembers() {
		return members;
	}
	public void setMembers(List<Entity> members) {
		this.members = members;
	}
	public Entity getOwner() {
		return owner;
	}
	public void setOwner(Entity owner) {
		this.owner = owner;
	}
	public boolean isPublic() {
		return Public;
	}
	public void setPublic(boolean public1) {
		Public = public1;
	}
}
