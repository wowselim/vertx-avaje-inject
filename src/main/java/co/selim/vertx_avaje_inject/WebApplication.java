package co.selim.vertx_avaje_inject;

import co.selim.vertx_avaje_inject.account.ApiVerticle;
import co.selim.vertx_avaje_inject.email.EmailVerticle;
import io.avaje.inject.BeanScope;
import io.vertx.core.Deployable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class WebApplication {

  private static final Logger LOG = LoggerFactory.getLogger(WebApplication.class);
  private static final int cpuCount = Runtime.getRuntime().availableProcessors();

  private List<String> deploymentIds;

  public void start(Vertx vertx) {
    BeanScope beanScope = BeanScope.builder()
      .bean(Vertx.class, vertx)
      .bean(EventBus.class, vertx.eventBus())
      .build();

    List<Class<? extends Deployable>> deployables = List.of(
      ApiVerticle.class,
      EmailVerticle.class
    );
    DeploymentOptions deploymentOptions = new DeploymentOptions().setInstances(cpuCount);

    List<Future<?>> deployments = deployables
      .stream()
      .map(c -> vertx.deployVerticle(() -> beanScope.get(c), deploymentOptions))
      .collect(Collectors.toList());

    Future.all(deployments)
      .onSuccess(future -> {
        deploymentIds = future.list();
        LOG.info("Deployment successful");
      })
      .onFailure(t -> {
        t.printStackTrace(System.err);
        vertx.close();
      });
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    new WebApplication().start(vertx);
  }
}
