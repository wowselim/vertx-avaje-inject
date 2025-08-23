package co.selim.vertx_avaje_inject;

import co.selim.vertx_avaje_inject.email.EmailVerticle;
import co.selim.vertx_avaje_inject.web.ApiVerticle;
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

public class Application {

  private static final Logger LOG = LoggerFactory.getLogger(Application.class);
  private static final int cpuCount = Runtime.getRuntime().availableProcessors();

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
      .onSuccess(ignore -> {
        LOG.info("Deployment successful");
      })
      .onFailure(t -> {
        LOG.error("Deployment failed", t);
        vertx.close();
      });
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    new Application().start(vertx);
  }
}
