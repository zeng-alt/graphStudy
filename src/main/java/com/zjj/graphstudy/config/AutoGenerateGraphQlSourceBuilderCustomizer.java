package com.zjj.graphstudy.config;

import com.google.common.collect.Lists;
import com.zjj.annotations.GQuery;
import com.zjj.graphstudy.dao.BaseRepository;
import com.zjj.graphstudy.utils.RepositoryUtils;
import graphql.language.*;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年09月09日 15:19
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AutoGenerateGraphQlSourceBuilderCustomizer implements GraphQlSourceBuilderCustomizer {

    private final List<BaseRepository> list;

    @Override
    public void customize(GraphQlSource.SchemaResourceBuilder builder) {
        builder.schemaFactory((t, r) -> {

            List<Definition> definitions = Lists.newArrayList();

            for (BaseRepository baseRepository : list) {
                RepositoryMetadata repositoryMetadata = RepositoryUtils.getRepositoryMetadata(baseRepository);
                GQuery mergedAnnotation = AnnotatedElementUtils.findMergedAnnotation(RepositoryUtils.getEntityType(baseRepository), GQuery.class);
                String type = RepositoryUtils.getGraphQlTypeName(baseRepository);
                String graphqlType = type;
                if (StringUtils.isBlank(type)) {
                    continue;
                }
                if (type.charAt(type.length() - 1) == 's') {
                    type = type.substring(0, type.length() - 1);
                }
                String capitalize = StringUtils.capitalize(type);
                String uncapitalize = StringUtils.uncapitalize(type);
            }



            ObjectTypeExtensionDefinition queryTypeDefinition = ObjectTypeExtensionDefinition.newObjectTypeExtensionDefinition()
                    .name("Query")
//                    .implementz(TypeName.newTypeName("").build())
                    .fieldDefinition(FieldDefinition.newFieldDefinition()
                            .name("hello")
                            .inputValueDefinition(InputValueDefinition.newInputValueDefinition().name("id").type(TypeName.newTypeName("String").build()).build())
                            .type(TypeName.newTypeName("Users").build())
//                                    .comments(List.of(new Comment("id不能为空", new SourceLocation(1, 1))))
                            .build())
                    .build();

            ObjectTypeDefinition userTypeDefinition = ObjectTypeDefinition.newObjectTypeDefinition()
                    .name("User")
                    .fieldDefinition(FieldDefinition.newFieldDefinition()
                            .name("id")
                            .type(TypeName.newTypeName("ID").build())
                            .build())
                    .fieldDefinition(FieldDefinition.newFieldDefinition()
                            .name("name")
                            .type(TypeName.newTypeName("String").build())
                            .build())
                    .build();

            InputObjectTypeDefinition inputObjectTypeDefinition = InputObjectTypeDefinition.newInputObjectDefinition()
                    .name("UserInput")

                    .build();




            Document document = Document
                    .newDocument()
                    .definitions(definitions)
                    .build();

            TypeDefinitionRegistry typeRegistry = new SchemaParser().buildRegistry(document);
            t = t.merge(typeRegistry);
//                    t.add("Query", "findUserById", (DataFetcher) environment -> "id: " + environment.getArgument("id"));
//                    t.
            return new SchemaGenerator().makeExecutableSchema(t, r);
        });
    }
}
