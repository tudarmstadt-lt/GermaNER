import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tu.darmstadt.lt.ner.model.Entity;
import de.tu.darmstadt.lt.ner.model.Relation;

public class EntityTest {
	public static void main(String[] args) {
		Entity e1 = new Entity("Seid", "a");
		Entity e2 = new Entity("seid", "a");
		Entity e3 = new Entity("Seid", "b");

		System.out.println(e1.equals(e2));
		System.out.println(e1.equals(e3));
		
		List<Entity> ents = new ArrayList<>();
		ents.add(e1);
		
		System.out.println("content"+ents.contains(e2));

		Relation r1 = new Relation(e1, e2);
		Relation r2 = new Relation(e2, e1);
		Relation r3 = new Relation(e1, e3);

		System.out.println(r1.equals(r2));
		System.out.println(r1.equals(r3));
		
		Set<Relation> rels = new HashSet<>();
		rels.add(r1);
		System.out.println(rels.contains(r2));
	}
}
