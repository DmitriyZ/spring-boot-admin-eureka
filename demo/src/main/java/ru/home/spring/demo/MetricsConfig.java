package ru.home.spring.demo;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import org.springframework.boot.actuate.autoconfigure.ExportMetricReader;
import org.springframework.boot.actuate.metrics.reader.MetricReader;
import org.springframework.boot.actuate.metrics.reader.MetricRegistryMetricReader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Created by dzaets on 09.02.2017.
 */
@Configuration
@EnableMetrics
public class MetricsConfig {


    @Bean
    @ExportMetricReader
    public MetricReader metricReader() {
        return new MetricRegistryMetricReader(metricRegistry());
    }

    private MetricRegistry metricRegistry() {
        MetricRegistry metricRegistry = new MetricRegistry();
        metricRegistry.register("jvm.gc", new MemoryUsageGaugeSet());
        metricRegistry.register("jvm.mem", new GarbageCollectorMetricSet());
        metricRegistry.register("jvm.threads", new ThreadStatesGaugeSet());
        return metricRegistry;
    }

    /**
     * Выводить метрики в консоль, если выключен экспорт в statsd
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(value = "spring.metrics.export.enabled", havingValue = "false")
    public ConsoleReporter consoleReporter() {
        ConsoleReporter.Builder builder = ConsoleReporter.forRegistry(metricRegistry());
        ConsoleReporter reporter = builder.build();
        reporter.start(10000, TimeUnit.MILLISECONDS);
        return reporter;
    }

}