package de.tu.darmstadt.lt.ner.model;

public class Relation {

	public Relation() {

	}

	public Relation(Entity entity1, Entity entity2) {
		this.entity1 = entity1;
		this.entity2 = entity2;
	}

	long id;
	Entity entity1;
	Entity entity2;
	long frequency;
	boolean isBlackListed;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Entity getEntity1() {
		return entity1;
	}

	public void setEntity1(Entity entity1) {
		this.entity1 = entity1;
	}

	public Entity getEntity2() {
		return entity2;
	}

	public void setEntity2(Entity entity2) {
		this.entity2 = entity2;
	}

	public long getFrequency() {
		return frequency;
	}

	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}

	public boolean isBlackListed() {
		return isBlackListed;
	}

	public void setBlackListed(boolean isBlackListed) {
		this.isBlackListed = isBlackListed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entity1 == null) ? 0 : entity1.hashCode());
		result = prime * result + ((entity2 == null) ? 0 : entity2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Relation other = (Relation) obj;
		if (entity1 == null) {
			if (other.entity1 != null)
				return false;
		} else if (!entity1.equals(other.entity1))
			return false;
		if (entity2 == null) {
			if (other.entity2 != null)
				return false;
		} else if (!entity2.equals(other.entity2))
			return false;
		return true;
	}

	

}
