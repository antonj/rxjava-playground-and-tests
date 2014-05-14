@Grab(group='com.netflix.rxjava', module='rxjava-groovy', version='0.17.+')

import rx.Observable;
import rx.Subscription;
import java.util.concurrent.TimeUnit;

def output = Observable.create(
  { subscriber ->
    println('subscribing to output')
    Thread.start(
      {
        Thread.sleep(400);
        subscriber.onNext(1);
        Thread.sleep(200);
        subscriber.onNext(2);
        Thread.sleep(200);
        subscriber.onNext(3);
        Thread.sleep(200);
        subscriber.onNext(4);
        Thread.sleep(200);
        subscriber.onNext(5);
        Thread.sleep(200);
        subscriber.onNext(6);
        Thread.sleep(200);
        subscriber.onNext(7);
        Thread.sleep(2000000); // Complete will trigger takeUntil
        subscriber.onCompleted()
      })
    subscriber.add(
      [unsubscribe : {
          println('unsubscribing output') }
        ] as Subscription)
  }).publish();


input.takeUntil(output)
.subscribe({ arg -> println('Got result: ' + arg + " ================") },
           { e -> println(e.getMessage()) },
           { println('complete') });

output.connect(); // Start sending input vals

Thread.sleep(2000000) // just hang on a while
println('shutdown')