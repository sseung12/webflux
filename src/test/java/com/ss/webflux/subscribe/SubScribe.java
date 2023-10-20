package com.ss.webflux.subscribe;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class SubScribe {


    @Test
    void 정의(){
        Flux.fromIterable(List.of(1,2,3,4,5))
                .doOnNext( value ->{
                    log.info("value : " + value);
                }).subscribe();
    }

    @Test
    void subscribe의_함수형_인터페이스처리(){

        Flux.fromIterable(List.of(1,2,0, 4,5))
                .map(x ->  10 / x)
                .subscribe(
                        value  -> log.info("value : " + value),
                        error -> log.error("error : "+ error),
                        ()-> log.info("complete")
                );

        Flux.range(1,5)
                .doOnNext( value -> log.info("value : "+value))
                .doOnError(error -> log.error("error : "+error))
                .doOnComplete(()-> log.info("complete"))
                .subscribe();
    }

    @Test
    void subScriber의_구현체전달(){

        // unbounded request : 즉시 publisher의 max 개수만큼 요청

        Flux.fromIterable(List.of(1,2,3,4,5))
                .map(x ->  10 / x)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Integer integer) {

                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Test
    void 미리_구현한_subscribe(){

        var subscriber = new BaseSubscriber<>(){
            @Override
            protected void hookOnComplete() {
                log.info("complete");
            }

            @Override
            protected void hookOnNext(Object value) {
                log.info("value : "+ value);
            }
        };

        Flux.fromIterable(List.of(1,2,3,4,5))
                .subscribe(subscriber);
        subscriber.request(1);
        subscriber.cancel();

    }


    /*
        unbounded request는 Publisher에게 가능한 빠르게 아이템을 전달해달라는 요청
        request(Long.Max_Value)로 실행
        backpressure를 비활성화

        아무것도 넘기지 않는 그리고 lambda 기반의 subscribe()
        BaseSubscriber의 hookOnSubscribe를 그대로 사용
        block(), blockFirst(),blockLast() blocking 연산자
        toiterabale(), toStream() 등의 연산자
     */
    @Test
    void buffer(){

        var subscriber = new BaseSubscriber<>(){
            private Integer count = 0;

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(4);
            }

            @Override
            protected void hookOnNext(Object value) {
                log.info("value : "+ value);
//                if( ++count ==2)
//                    cancel();
            }
            @Override
            protected void hookOnComplete() {
                log.info("complete");
            }

        };

        Flux.fromStream(IntStream.range(0,10).boxed())
                .buffer(3)
                .subscribe(subscriber);
    }

    @Test
    void take연산자(){


        var subscriber = new BaseSubscriber<>(){

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(2);
            }

            @Override
            protected void hookOnNext(Object value) {
                log.info("value : "+ value);

            }
            @Override
            protected void hookOnComplete() {
                log.info("complete");
            }

        };

        Flux.fromStream(IntStream.range(0,10).boxed())
                .take(5,true)
                .subscribe(subscriber);
    }
}
