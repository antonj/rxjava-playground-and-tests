@Grab(group='com.netflix.rxjava', module='rxjava-groovy', version='0.17.+')

import rx.Observable;
import java.util.concurrent.TimeUnit;

def oneAndFail() {
  return Observable.create({ subscriber ->
    println('subscribing')
    subscriber.onNext(1);
    subscriber.onError(new Error("fail"));
  });
}

// oneAndFail()
//   .onErrorResumeNext( oneAndFail() )//.delaySubscription(2, TimeUnit.SECONDS) )
//   .subscribe({ arg -> println(arg) },
//              { e -> println(e.getMessage()) },
//              { println('complete') });

println('================ only retry')

// oneAndFail()
// .onErrorResumeNext(
//   oneAndFail()
//   // .delaySubscription(2000, TimeUnit.MILLISECONDS)
//   .retry(3)
// ).subscribe({ arg -> println('-> ' + arg) },
//               { e -> println(e.getMessage()) },
//               { println('complete') });

oneAndFail().retry(5).flatMap({ n -> Observable.from(n * 2) })
.subscribe({ arg -> println('-> ' + arg) },
              { e -> println(e.getMessage()) },
              { println('complete') });

Thread.sleep(300000)