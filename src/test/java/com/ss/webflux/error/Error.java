package com.ss.webflux.error;

import com.ss.webflux.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.function.Function;

@Slf4j
public class Error{

    /*
        Reactive streams에서 onError 이벤트가 발생하면, 더이상 onNext, onComplete 이벤트를 생상하지 않고 종료
        onError 이벤트가 발생하면 onError 이벤트를 아래로 전파

        값을 처리하기 위해
        - 고정된 값을 반환하거나
        - publisher를 반화하거나
        - onComplete 이벤트로 변경하거나
        - 다른 에러로 변환하거나

        에러핸들링이 없는 경우
        - onErrorDrop을 호출
     */

    @Test
    void 에러핸들이_없는경우(){
        Flux.create( sink ->{
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    throw new RuntimeException(e);
                }
                sink.error(new RuntimeException("error"));
            }
        ).subscribe();
    }

    @Test
    void errorConsumer(){
        Flux.error(new RuntimeException("error"))
                .subscribe(value ->{
                    log.info("value :"+value);
                },error -> {
                    log.error("error : "+error);
                });
    }

    // onErrorReturn 고정된 값을 반환
    @Test
    void onErrorReturn(){
        Flux.error(new RuntimeException("error"))
                .onErrorReturn("Hello World!")
                .subscribe(value ->{
                    log.info("value :"+value);
                },error -> {
                    log.error("error : "+error);
                });
    }

    @Test
    void onErrorReturn의_문제점(){
        log.info("start");
        Flux.just(7,8,9)
                .onErrorReturn(shouldDoOnError())
                .subscribe(value ->{
                    log.info("value : "+value);
                });
        log.info("end");
    }
    private static int shouldDoOnError(){
        log.info("shouldDoOnError");
        return 1;
    }


    /*
        onErrorResume
        - onError 이벤트를 처리하기 위해 publisher를 반환하는 추상함수를 실행.
        - 해당 publisher의 onNext, onError, onComplete 이벤트를 아래로 전달
       - !! error 이벤트가 발생한 상황에서만 apply 실행
     */
    @Test
    void onErrorResume(){

        Flux.error(new RuntimeException("error"))
                .onErrorResume(new Function<Throwable, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Throwable throwable) {
                        return Flux.just(1,2,3);
                    }
                }).subscribe(value ->{
                    log.info("value : "+value);
                });

    }

    @Test
    void onErrorResumeWithMethod(){

        Flux.just(1)
                .onErrorResume(e -> Mono.just(shouldDoOnError()))
                .subscribe(value ->{
                    log.info("value : "+value);
                });

    }

    @Test
    void onErrorComplete(){
        Flux.create(sink ->{
            sink.next(1);
            sink.error(new RuntimeException("error"));
            sink.next(2);
            sink.error(new RuntimeException("error"));
        }).onErrorComplete()
                .subscribe(value ->{
                    log.info("value : "+ value);
                },error ->{
                  log.error("error : "+error);
                },() ->{
                    log.info("complete");
                }
            );
    }

    /*
        onErrorMap
        onError 이벤트를 처리하기 위해 다른 에러로 변환
        다른 이벤트로 변환하여, 저수준의 에러를 고수준의 에러, 비지니스 로직과 관련된 에러로 변환 가능
        변환만 하기때무네 ,추가적인 에러 핸들리은 필요하다.
     */

    @Test
    void onErrorMap(){
        Flux.error(new IOException("fail to load file"))
                .onErrorMap(e -> new CustomException("custom"))
                .subscribe(value ->{
                    log.info("value = "+value);
                },error -> {
                  log.error("error : "+error);
        });
    }
}
