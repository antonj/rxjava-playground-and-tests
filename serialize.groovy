@Grab(group='com.netflix.rxjava', module='rxjava-groovy', version='0.17.+')

import rx.Observable;
import rx.Subscription;
import java.util.concurrent.TimeUnit;

def oneWithDelay(final delay) {
  return Observable.create(
    { subscriber ->
      println('s: subscribing: ' + delay)
      Thread.start(
        {
          Thread.sleep(delay)
          subscriber.onNext(delay);
          subscriber.onCompleted();
        })
      subscriber.add(
        [unsubscribe : {
            println('x: unsubscribe: ' + delay) }
          ] as Subscription)
    })
}

// def input = Observable.from(1,2,3,4,5,6,7).publish()

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
  }).publish(); // Multicast otherwise takeUntil will subscribe unsubscribe


input.flatMap(
  {i ->
    println('flatMapping('+ i + ')');
    // switch between fast and slow responces 100, 2000, 300, 4000,... delays
    ( (i % 2) ? oneWithDelay(i * 100) : oneWithDelay(i * 1000) )
       .takeUntil(input) // Skipp delayed call if new input is received
  })
  .subscribe({ arg -> println('Got result: ' + arg + " ================") },
             { e -> println(e.getMessage()) },
             { println('complete') });

input.connect(); // Start sending input vals

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