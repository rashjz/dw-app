package dw.ms.app;

import dw.ms.app.auth.AppAuthorizer;
import dw.ms.app.auth.AppBasicAuthenticator;
import dw.ms.app.core.Person;
import dw.ms.app.core.Template;
import dw.ms.app.core.User;
import dw.ms.app.db.PersonDAO;
import dw.ms.app.health.TemplateHealthCheck;
import dw.ms.app.resources.PeopleResource;
import dw.ms.app.resources.PersonResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import java.util.Map;

public class AppRunner extends Application<AppConfiguration> {
    private final HibernateBundle<AppConfiguration> hibernateBundle =
            new HibernateBundle<AppConfiguration>(Person.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(AppConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };


    public static void main(String[] args) throws Exception {
        new AppRunner().run(args);
    }


    @Override
    public String getName() {
        return "application";
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

//        bootstrap.addCommand(new RenderCommand());
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new MigrationsBundle<AppConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(AppConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new ViewBundle<AppConfiguration>() {
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(AppConfiguration configuration) {
                return configuration.getViewRendererConfiguration();
            }
        });
    }


    @Override
    public void run(AppConfiguration configuration, Environment environment) {
        final PersonDAO dao = new PersonDAO(hibernateBundle.getSessionFactory());
        final Template template = configuration.buildTemplate();
        //****** Dropwizard admin health check ***********/
        environment.healthChecks().register("template", new TemplateHealthCheck(template));

//        environment.admin().addTask(new EchoTask());
//        environment.jersey().register(DateRequiredFeature.class);

        //****** Dropwizard security - custom classes ***********/
        environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                .setAuthenticator(new AppBasicAuthenticator())
                .setAuthorizer(new AppAuthorizer())
                .setRealm("BASIC-AUTH-REALM")
                .buildAuthFilter()));

        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);


//        environment.jersey().register(new HelloWorldResource(template));
//        environment.jersey().register(new ViewResource());
//        environment.jersey().register(new ProtectedResource());
        environment.jersey().register(new PeopleResource(dao));
        environment.jersey().register(new PersonResource(dao));
//        environment.jersey().register(new FilteredResource());
    }
}
