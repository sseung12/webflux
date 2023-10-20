package com.ss.webflux.etc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class Etc {

    @Test
    void map(){

        Flux.range(1,5)
                .map( value -> value * 2)
                .doOnNext( v -> {
                    log.info("v : "+ v);
                }).subscribe();

        Flux.range(1,5)
                .mapNotNull( value -> {
                    if(value % 2 ==0){
                        return value;
                    }
                    return null;
                })
                .doOnNext( v -> {
                    log.info("v : "+ v);
                }).subscribe();
    }

    @Test
    void flatMap(){

        Flux.range(1,5)
                .flatMap( value ->{
                    return Flux.range(1,2)
                            .map(inner ->value + " : " + inner)
                            .publishOn(Schedulers.parallel());
                }).doOnNext(value -> log.info("doOnNext : " + value))
                .subscribe();
    }

    @Test
    void filter(){

        Flux.range(1,5)
                .filter( v -> v% 2==0)
                .doOnNext( v -> log.info("v : " +v))
                .subscribe();
    }

    @Test
    void take_takeLsat(){

        Flux.range(1,10)
                .take(5)
                .doOnNext( v -> log.info("take : "+ v))
                .subscribe();

        Flux.range(1,10)
                .takeLast(5)
                .doOnNext( v -> log.info("takeLast : "+v))
                .subscribe();
    }

    @Test
    void skip_skipLast(){

        Flux.range(1,10)
                .skip(5)
                .doOnNext( v -> log.info("skip : "+ v))
                .subscribe();

        Flux.range(1,10)
                .skipLast(5)
                .doOnNext( v -> log.info("skipLast : "+v))
                .subscribe();
    }

    /*
        collectList
        - next이벤트가 전달되면 내부의 item을 저장
        - complete 이벤트가 전달되면 저장했떤 item들을 list형태로 만들고 아래에 onNext발행
        - 즉시 complte 이벤트 발행
        - Flux2Mono에 유용
     */
    @Test
    void collectList(){
        Flux.range(1,5)
                .collectList()
                .doOnNext(value -> log.info("v : "+value))
                .subscribe();
    }
}
