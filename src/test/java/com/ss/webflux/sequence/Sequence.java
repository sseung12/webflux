package com.ss.webflux.sequence;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

@Slf4j
public class Sequence {

    @Test
    void just(){

        Mono.just(1)
                .subscribe(value -> {
                            log.info("value : " + value);});

        Flux.just(1,2,3,4)
                .subscribe(value -> {
                    log.info("value : " +value);});
    }

    /*
        error -> 실제 자바 error도 handling할수 있는지?
     */
    @Test
    void error(){

        Flux.error( new RuntimeException("error"))
                .subscribe( value ->{
                    log.info("value : "+value);
                }, error ->{
                    log.error("error : "+error);
                });
    }

    @Test
    void empty(){
        // 바로 complete를 호출하기 때문에, event의 마무리가 필요할때
        Flux.empty()
                .subscribe( value ->{
                    log.info("value : "+value);
                }, null, () ->
                        log.info("complete"));
    }


    /*
        동기적으로 Flux를 생성
        stateSupplier 초기값을 제공하는 Callable
        sink를 구현
     */
    @Test
    void generate(){

        Flux.generate(
                () -> 0,
                (state, sink) ->{
                    sink.next(state);
                    if (state == 9) {
                        sink.complete();
                    }
                    return state + 1;
                }
        ).subscribe(value ->{
            log.info("value : "+value);
        },error ->{
            log.error("error : "+error);
        },() ->{
            log.info("complete");
        });

    }

    /*
        비동기적으로 Flux를 생성
        FluxSink를 노출
        명시적으로 next,error,complete 호출 가능
        sink와 다르게 여러번 next 호출 가능
        여러 thread에서 동시에 호출 가능
     */
    @Test
    void create(){

        Flux.create(sink ->{

            var task1 = CompletableFuture.runAsync(()->{
                for (int i = 0; i < 5; i++) {
                    sink.next("t1_"+i);
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.info("task1 is finished");
            });

            var task2 = CompletableFuture.runAsync(() ->{
                for (int i = 0; i < 10; i++) {
                    sink.next("t2_"+i);
                }
            });
            CompletableFuture.allOf(task1,task2).thenRun(sink::complete);
        }).subscribe(value ->{
            log.info("value : " + value);
        },error ->{
            log.error("error : "+error);
        },() ->{
            log.info("complete");
        });
    }


    /*
        독립적으로 sequence를 생성할 수 없고, 존재하는 source에 연결
        - 첫 번째 인자로 source의 item이 제공
        - 두 번째 인자로 SynchronousSink를 제공
        - sink의 next를 이용해서 현재 주어진 item을 전달할지 말지를 결정
        - 일종의 interceptor로 source의 item을 필터하거나 변경할 수있다.
     */
    @Test
    void handle(){
        Flux.fromStream(IntStream.range(1,10).boxed())
                .handle((value,sink)-> {
                    if (value % 2 == 0) {
                        sink.next(value);
                    }
                }).subscribe(value ->{
                    log.info("value : "+value);
                },error ->{
                    log.error("error : "+error);
                },() ->log.info("complete"));

    }
}
