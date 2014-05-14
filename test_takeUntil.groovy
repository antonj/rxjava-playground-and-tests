@Grab(group='com.netflix.rxjava', module='rxjava-groovy', version='0.17.+')

import rx.Observable;
import rx.Subscription;
import java.util.concurrent.TimeUnit;

def input = Observable.create(
  { subscriber ->
    println('subscribing to input')
    Thread.start(
      {
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
          println('unsubscribing input') }
        ] as Subscription)
  });


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

// RESULT
// $ groovy serialize.groovy 
// subscribing to input
// flatMapping(1)
// s: subscribing: 100
// Got result: 100 ================
// x: unsubscribe: 100
// flatMapping(2)
// s: subscribing: 2000
// flatMapping(3)
// s: subscribing: 300
// x: unsubscribe: 2000
// flatMapping(4)
// s: subscribing: 4000
// x: unsubscribe: 300
// flatMapping(5)
// s: subscribing: 500
// x: unsubscribe: 4000
// flatMapping(6)
// s: subscribing: 6000
// x: unsubscribe: 500
// flatMapping(7)
// s: subscribing: 700
// x: unsubscribe: 6000
// Got result: 700 ================
// x: unsubscribe: 700