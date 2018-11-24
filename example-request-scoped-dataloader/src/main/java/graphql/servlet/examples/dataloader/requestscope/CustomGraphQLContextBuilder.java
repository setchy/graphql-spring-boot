package graphql.servlet.examples.dataloader.requestscope;

import graphql.servlet.DefaultGraphQLContext;
import graphql.servlet.GraphQLContext;
import graphql.servlet.GraphQLContextBuilder;
import graphql.servlet.GraphQLHttpContext;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import java.util.concurrent.CompletableFuture;

@Component
public class CustomGraphQLContextBuilder implements GraphQLContextBuilder {

    private final CustomerRepository customerRepository;

    public CustomGraphQLContextBuilder(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public GraphQLContext build(HttpServletRequest req, HttpServletResponse response) {
        GraphQLContext context = GraphQLHttpContext.create(req, response);
        context.setDataLoaderRegistry(buildDataLoaderRegistry());

        return context;
    }

    @Override
    public GraphQLContext build() {
        GraphQLContext context = new DefaultGraphQLContext();
        context.setDataLoaderRegistry(buildDataLoaderRegistry());

        return context;
    }

    @Override
    public GraphQLContext build(Session session, HandshakeRequest request) {
        GraphQLContext context = GraphQLHttpContext.create(session, request);
        context.setDataLoaderRegistry(buildDataLoaderRegistry());

        return context;
    }

    private DataLoaderRegistry buildDataLoaderRegistry() {
        DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();
        dataLoaderRegistry.register("customerDataLoader",
                new DataLoader<Integer, String>(customerIds ->
                        CompletableFuture.supplyAsync(() ->
                                customerRepository.getUserNamesForIds(customerIds))));
        return dataLoaderRegistry;
    }
}
