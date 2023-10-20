package com.ss.webflux.context;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class Context {

    /*
        publisher와 subscribe사이의 공통된 정보를 공유하는 방법???
        ThreadLocal ?? 별도의 쓰레드에서 작업되는 pipeLine일 경우 , ThreadLocal은 무용지물

        Context
        - Context는 파이프라인 내부 어디에서든 접근이 가능한 key , value 집합소
        - value를 접근, 수정할 수 있는 수단을 제공
        - 읽기 전용인 ContextView와 쓰기를 할 수 있는 Context로 구분
     */

    /*
        contextWrite
        - Context를 인자로 받고, Context를 반환하는 함수형 인터페이스를 제공
        - 이를 통해서, 기존의 Context에 값을 추가하거나 변경,삭제 가능
        - Context는 immutable하기 때문에 각각의 작업은 새로운 Context를 생성 -> Thread safe하다??
     */


    @Test
    void contextWrite(){

//        Flux.just(1)
//                .flatMap( v -> ContextLogg.)
    }


    /*
        publisher를 생성하는 Consumer를 인자로 받아서 publihser를 생성하고, 생성되 publisher의 이벤틀르 아래로 전다.ㄹ
     */
    @Test
    void defer(){

        Mono.defer(() -> Mono.just(1)).subscribe(value ->{
            log.info("value : "+value);
        });
    }
}
