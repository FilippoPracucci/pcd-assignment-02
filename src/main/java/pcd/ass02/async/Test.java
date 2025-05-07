package pcd.ass02.async;

import io.vertx.core.Vertx;

/**
 * @author Bedeschi Federica   federica.bedeschi4@studio.unibo.it
 * @author Pracucci Filippo    filippo.pracucci@studio.unibo.it
 */
public class Test {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new AnalyserVerticle());
    }

}
