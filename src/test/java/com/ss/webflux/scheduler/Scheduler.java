package com.ss.webflux.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

@Slf4j
public class Scheduler {

    /*
        reactor에서 아무런 설정을 하지 않았다면, publihser는 subscribe를 호출한 caller의 쓰레드에서 실행된다.
        subscribe에 제공된 lambda 혹은 Scheduler 또한 caller의 쓰레드에서 실행

     */


    /*
        Scheduler
        - Publish 혹은 Subscribe에 어떤 scheduler가 적용되어 있는가에 딸서, task를 실행하는
           쓰레드 풀이 결정
        - 즉시 task를 실행하거나 delay를 두고 실행 가능
        - ImmedateScheduler
        - SingleScheduler
        - ParelleScheduler;
     */


    /*
        subscirbe를 호출한 caller 쓰레드에서 즉시 실행
        별도로 Schedulers 넘기지 않았을대, 이 값 사용 기본값
     */
    @Test
    void Schedulers_immediate(){
        Flux.create( sink ->{
            for (int i = 0; i < 5; i++) {
                log.info("next : {}",i);
                sink.next(i);
            }
        }).subscribeOn(Schedulers.immediate())
                .subscribe( value ->{
                    log.info("value : "+ value);
                });

    }

    /*
        Schedulers.single
        - 캐싱된 1개 크기의 쓰레드풀을 제공
        - 모든 publish, subscribe가 하나의 쓰레드에서 실행
     */
    @Test
    void Schedulers_single() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            final int idx = i;
            Flux.create(sink ->{
                log.info("next : {}",idx);
            }).subscribeOn(Schedulers.single())
                    .subscribe(System.out::println);
        }
        Thread.sleep(1000);
    }


    /*
        - 캐싱된 n개 크기의 쓰레드 풀을 제공
        - 기본적으로 cpu 코어 수만큼의 크기를 갖는다.
     */
    @Test
    void Schedulers_parallel() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            final int idx = i;
            Flux.create(sink ->{
                        log.info("next : {}",idx);
                    }).subscribeOn(Schedulers.parallel())
                    .subscribe(value ->{
                        log.info("value : "+value);
                    });
        }
        Thread.sleep(2000);
    }
}
