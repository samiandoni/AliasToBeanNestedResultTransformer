AliasToBeanNestedResultTransformer
==================================
Support multi-level nested associations.

You can use this class as criteria trnsformer in Hibernate to bind associated beans to the target bean.

Usage Example:
--------------

    class Person {
      private Long id;
      private String name;
      private Car car;
      // getters and setters
    }

    class Car {
      private Long id;
      private String color;
      private Part part;
      // getters and setters
    }
    
    class Part{
    	private String name;
    	private String type;
    }
    
    List<Person> getPeople() {
      ProjectionList projections = Projections.projectionList()
		.add(Projections.id().as("id"))
		.add(Projections.property("name").as("name"))
		.add(Projections.property("c.id").as("car.id"))
		.add(Projections.property("c.color").as("car.color"))
		.add(Projections.property("p.name").as("car.part.name"))
		.add(Projections.property("p.type").as("car.part.type"));
    
      Criteria criteria = getCurrentSession().createCriteria(Person.class)
        .createAlias("car", "c")
        .setProjection(projections)
        .setResultTransformer(new AliasToBeanNestedResultTransformer(Person.class));

      return (List<Person>) criteria.list();
    }

	// each car of Person will be populated
    
This is working for one-to-one and many-to-one associations (or beans).

Test case
----------
	@Test
	public void testAliasToBeanNestedResultTransformer(){
	    Object[] tuple = new Object[]{"Aid","c2id","bid","cid", "cvalue"};
	    String[] aliases = new String[]{"id","c.id","b.id", "b.c.id", "b.c.c"};
	    AliasToBeanNestedResultTransformer a2b = new AliasToBeanNestedResultTransformer(A.class);
	    A a = (A)a2b.transformTuple(tuple, aliases);
	    System.out.println(a);
	}
	
	public class A extends Base{
		private B b;
		private C c;
		
		public B getB() {
			return b;
		}
	
		public void setB(B b) {
			this.b = b;
		}
	
		
	
		@Override
		public String toString() {
			return String.format("A [b=%s, c=%s]", b, c);
		}
	
		public C getC() {
			return c;
		}
	
		public void setC(C c) {
			this.c = c;
		}
		
		
	}
	
	public class B extends Base{
		private C c;
	
		public C getC() {
			return c;
		}
	
		public void setC(C c) {
			this.c = c;
		}
	
		@Override
		public String toString() {
			return String.format("B [c=%s]", c);
		}
		
	}
	
	public class C extends Base{
		private String c;
		private A a;
		public String getC() {
			return c;
		}
	
		public void setC(String c) {
			this.c = c;
		}
	
		public A getA() {
			return a;
		}
	
		public void setA(A a) {
			this.a = a;
		}
	
		@Override
		public String toString() {
			return String.format("C [c=%s, a=%s]", c, a);
		}
		
		
	}
	
	public class Base{
		private String id;
	
		public String getId() {
			return id;
		}
	
		public void setId(String id) {
			this.id = id;
		}
	
		@Override
		public String toString() {
			return String.format("Base [id=%s]", id);
		}
		
	}