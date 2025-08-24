package co.selim.vertx_avaje_inject.web;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.Utf8ByteOutput;
import io.avaje.jsonb.Jsonb;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

public final class RoutingContexts {

  private static final CharSequence APPLICATION_JSON =
    HttpHeaders.createOptimized("application/json; charset=utf-8");
  private static final CharSequence TEXT_HTML =
    HttpHeaders.createOptimized("text/html; charset=utf-8");

  private static final Jsonb jsonb = Jsonb.instance();
  private static final TemplateEngine templateEngine = TemplateEngine.createPrecompiled(ContentType.Html);

  static {
    templateEngine.setBinaryStaticContent(true);
  }

  private RoutingContexts() {
  }

  public static void respondWithJson(RoutingContext routingContext, int statusCode, Object body) {
    routingContext
      .response()
      .setStatusCode(statusCode)
      .putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
      .end(jsonb.toJson(body));
  }

  public static void respondWithJson(RoutingContext routingContext, Object body) {
    respondWithJson(routingContext, 200, body);
  }

  public static <T> T getBodyAsPojo(RoutingContext routingContext, Class<T> pojoClass) {
    String json = routingContext.body().asString();
    return jsonb.type(pojoClass)
      .fromJson(json);
  }

  public static void respondWithHtml(RoutingContext ctx, String template, Map<String, Object> model) {
    try {
      Utf8ByteOutput output = new Utf8ByteOutput();
      templateEngine.render(template + ".jte", model, output);
      Buffer buffer = Buffer.buffer(output.getContentLength());
      output.writeTo(buffer::appendBytes);
      ctx.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, TEXT_HTML)
        .end(buffer);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
