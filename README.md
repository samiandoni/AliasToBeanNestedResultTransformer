AliasToBeanNestedResultTransformer
==================================

You can use this class as criteria trnsformer in Hibernate to bind associated beans to the target bean.

Usage Example:
--------------

> class Person {
>   private Long id;
>   private String name;
>   private Car car;
>   
>   // getters and setters
> }
