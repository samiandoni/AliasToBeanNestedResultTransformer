AliasToBeanNestedResultTransformer
==================================

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
      // getters and setters
    }
    
    List<Person> getPeople() {
      ProjectionList projections = Projections.projectionList()
		    .add(Projections.id().as("id"))
			  .add(Projections.property("name").as("name"))
			  .add(Projections.property("c.id").as("car.id"))
			  .add(Projections.property("c.color").as("car.color"));
    
      Criteria criteria = getCurrentSession().createCriteria(Person.class)
        .createAlias("car", "c")
        .setProjection(projections)
        .setResultTransformer(new AliasToBeanNestedResultTransformer(Person.class));

        return (List<Person>) criteria.list();
    }
    
This is working for one-to-one and many-to-one associations (or beans).

We can develop it to include collections and multi-level nested associations.
