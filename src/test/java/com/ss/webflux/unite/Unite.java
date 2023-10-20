package com.ss.webflux.unite;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
public class Unite {


    /*
        concat
        Publisher를 다른 Publisher와 합치는 연산자
        앞의 publisher가 onComplete 이벤트를 전달하면 다음 publisher를 subscribe
        각각의 publisher의 순서가 보장된다.
     */
    @Test
    void concat() throws InterruptedException {
        Flux<Integer> flux1 = Flux.range(1, 3)
                .doOnSubscribe(value -> {
                    log.info("do On subscribe 1");
                }).delayElements(Duration.ofMillis(100));

        Flux<Integer> flux2 = Flux.range(10, 3)
                .doOnSubscribe(value -> {
                    log.info("do On subscribe 2");
                }).delayElements(Duration.ofMillis(100));

        Flux.concat(flux1,flux2)
                .doOnNext( value -> log.info("value : "+value))
                .subscribe();

        Thread.sleep(1000);
    }


    /*
        merge
        publisher를 다른 publisher와 합친는 연산자.
        모든 publihser를 바로  subscribe
        각각 publisher의 onNext 이벤트가 동시에 도달
        Publihser의 순서가 보장되지 않는다.
     */
    @Test
    void merge() throws InterruptedException {
        Flux<Integer> flux1 = Flux.range(1, 3)
                .doOnSubscribe(value -> {
                    log.info("do On subscribe 1");
                }).delayElements(Duration.ofMillis(100));

        Flux<Integer> flux2 = Flux.range(10, 3)
                .doOnSubscribe(value -> {
                    log.info("do On subscribe 2");
                }).delayElements(Duration.ofMillis(100));

        Flux.merge(flux1,flux2)
                .doOnNext( value -> log.info("value : "+value))
                .subscribe();

        Thread.sleep(1000);
    }

    /*
        mergeSequential
        - Publisher를 다른 Publisher와 합치는 연산자
        - 모든 Publisher를 바로 subscribe
        - 각각 Publisher의 onNext 이벤트가 동시에 도달
        - merge와 다르게 내부에서 재정렬하여 순서보장
     */
    @Test
    void mergeSequential() throws InterruptedException {
        Flux<Integer> flux1 = Flux.range(1, 3)
                .doOnSubscribe(value -> {
                    log.info("do On subscribe 1");
                }).delayElements(Duration.ofMillis(100));

        Flux<Integer> flux2 = Flux.range(10, 3)
                .doOnSubscribe(value -> {
                    log.info("do On subscribe 2");
                }).delayElements(Duration.ofMillis(100));

        Flux.mergeSequential(flux1,flux2)
                .doOnNext( value -> log.info("value : "+value))
                .subscribe();

        Thread.sleep(1000);
    }

    @Test
    void ds(){


    }
}
