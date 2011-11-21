package br.ufu.facom.network.dts.bean;

import java.util.ArrayList;
import java.util.List;

import br.ufu.facom.network.dts.topology.Element;

public class Entity {
	private String title;
	private List<Requirement> requirements;
	private List<Element> path;
	
	public Entity(){
		this.requirements = new ArrayList<Requirement>();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Requirement> getRequirements() {
		return requirements;
	}

	public void addRequirements(Requirement requirement) {
		this.requirements.add(requirement);
	}

	public List<Element> getPath() {
		return path;
	}

	public void setPath(List<Element> path) {
		this.path = path;
	}

	public void setRequirements(List<Requirement> requirements) {
		this.requirements = requirements;
	}
}
