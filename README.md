## graphql结合Querydsl
1. 在repository中使用Querydsl，使用repository继承QuerydslPredicateExecutor, 并加入@GraphQlRepository
2. 在schema中可以定义相关的query和Mutation, 只要在repository有对应的方法
3. 也可以通过配置来生成对就的mapped fields
    ```java
    builder.type("Query", b -> b
            .dataFetcher(uncapitalize, QuerydslDataFetcher.builder(baseRepository).single())
            .dataFetcher(uncapitalize + "s", QuerydslDataFetcher.builder(baseRepository).many())
            .dataFetcher("page" + capitalize + "s", QuerydslDataFetcher.builder(baseRepository).sortBy(Sort.by(Sort.Direction.DESC,"id")).scrollable())
            .dataFetcher("find" + capitalize + "ById", env -> baseRepository.findById(env.getArgument("id")).orElse(null))
    ```
   这是三个查询，分别是单个id，多个id，以及分页查询，对应的schema
   ```graphql
    type Query {
        users(id: [Long]): [Users]
        user(id: Long): Users
        pageUsers(first: Int, after: String, last: Int, before: String): UsersConnection
    }
   ```
