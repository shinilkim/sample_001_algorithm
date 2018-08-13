package ksi.scala

object LearnScala {
  def main(args: Array[String]): Unit = {
    //    main1(args)
    //    main2(args)
    //    main3(args)
    //    main4(args)
    //    main5(args)
    //    main6(args)
    //    main7(args)
    //    main8(args)
    //    main9(args)
    //    main10(args)
    //    main11(args)
    //    main12(args)
    main13(args)
  }
  
  def main13(args: Array[String]): Unit = {
    def printArray[K](array:Array[K]) = println(array.mkString("Array(" , ", ", ")"))
  }

  def main1(args: Array[String]): Unit = {
    println("============== main-01 ===================")
    println(1 + 2)
    println((1).+(2))

    var x = 1 + 2
    x += 30
    println(x)

    var a, b, c = 5
    println(s"$x is bigger than $a")
    printf("PI is %f\n", Math.PI);

    var r1 = 1 to 10
    val r2 = 1 until 10
    val r3 = 1 until 10 by 3
    println(s"r1: 1 to 10 → $r1")
    println(s"r2: 1 until 10 → $r2")
    println(s"r3: 1 until 10 by 3 → $r3")
    println(s"r1.toList → ${r1.toList}");
    val moreThan = r1.filter(_ > 4)
    println(s"r1.filter(_ > 4) → $moreThan");
    val doubleIt = r1.map(_ * 2)
    println(s"r1.amp(_*2) → $doubleIt")
  }

  def main2(args: Array[String]): Unit = {
    println("============== main-02 ===================")
    val num = -5
    val numAbs = num.abs
    val max5or7 = numAbs.max(7)
    val min5or7 = numAbs.min(7)
    println(numAbs)
    println(max5or7)
    println(min5or7)
  }

  def main3(args: Array[String]): Unit = {
    println("============== main-03 ===================")
    val reverse = "Scala".reverse
    val cap = "scala".capitalize
    val multi = "Scala! " * 7
    val int = "123".toInt
    println(s"$reverse")
    println(s"$cap")
    println(s"$multi")
    println(s"$int")
  }

  def main4(args: Array[String]): Unit = {
    println("============== main-04 ===================")
    println(s"${add4(1, 2)}")
    println(s"${add4WithoutReturn(3, 4)}")
    println(s"${add4WithoutBlock(5, 6)}")
  }
  def add4(x: Int, y: Int): Int = {
    return x + y // 일반적인 메서드
  }
  def add4WithoutReturn(x: Int, y: Int) = {
    x + y // return을 생략한 메서드 > return 적어주지 않으면 마지막 값이 return 된다.
  }
  def add4WithoutBlock(x: Int, y: Int) = x + y // 메서드가 한 줄일 경우에 중괄호 생략가능

  def main5(args: Array[String]): Unit = {
    println("============== main-05 ===================")
    val call1 = doWithOneAndTwo((x: Int, y: Int) => x + y) // 명시적으로 타입을 선언하는 익명함수
    val call2 = doWithOneAndTwo((x, y) => x + y) // 익명함수의 매개변수 타입이 이미 정했기 때문에 생략
    val call3 = doWithOneAndTwo(_ + _) // 매개변수의 순서대로 _에 대입
    println(call1, call2, call3)
  }
  def doWithOneAndTwo(f: (Int, Int) => Int) = {
    f(1, 2)
  }

  def main6(args: Array[String]): Unit = {
    println("============== main-06 ===================")
    def addTo1(x: Int, y: Int) = x + y // 메서드를 정의하는 방식
    val addTo2 = (x: Int, y: Int) => x + y // 익명함수
    val addTo3: (Int, Int) => Int = _ + _ // 익명함수를 정의하는 다른 방식
    val addTo4 = (_ + _): (Int, Int) => Int // 익명함수를 정의하는 다른 방식2

    println(s"${addTo1(42, 13)}")
    println(s"${addTo2(42, 13)}")
    println(s"${addTo3(42, 13)}")
    println(s"${addTo4(42, 13)}")
  }

  def main7(args: Array[String]): Unit = {
    println("============== main-07 ===================")
    val t1 = new Tuple3(1, "hello", true)
    var t2 = (1, "hello", false)
    println(s"t1: $t1")
    println(s"t2: $t2")

    var numbers = (1, 2, 3, 4)
    val sum = numbers._1 + numbers._2 + numbers._3 + numbers._4
    println(s"numbser: $numbers")
    println(s"sum: $sum")
  }

  def main8(args: Array[String]): Unit = {
    println("============== main-08 ===================")
    def swap(x: String, y: String) = (y, x)
    val (a, b) = swap("hello", "world")
    println(a, b)
  }

  def main9(args: Array[String]): Unit = {
    println("============== main-09 ===================")
    var (x, y, z, c, python, java) = (1, 2, 3, true, false, "no!")
    println(x, y, z, c, python, java)
    println(c)
  }

  def main10(args: Array[String]): Unit = {
    println("============== main-10 ===================")
    // while
    var i, sum = 0
    while (i < 10) {
      sum += i
      i += 1
    }
    println(s"while: $sum")
    // for
    sum = 0
    for (i <- 0 until 10) {
      sum += i
    }
    println(s"for: $sum")
    // scala sum
    sum = (0 until 10).sum
    println(s"scala-sum: $sum")
  }

  def main11(args: Array[String]): Unit = {
    println("============== main-11 ===================")
    for (a <- 1 to 2) {
      for (b <- 10 to 11) {
        println(s"A1: $a, $b")
      }
    }
    for (a <- 1 to 2; b <- 10 to 11) {
      println(s"A2: $a, $b")
    }
  }

  def main12(args: Array[String]): Unit = {
    if (true) println("한 줄은 {괄호}를 생략할 수 있습니다.")
    if (1 + 1 == 20) {
      print("여러 줄은 ")
      print("{괄호}가 필요합니다.")
    } else if (1 - 1 == 0) println("이상한 나라의 앨리스")
    else println("else")

    val breakfast = if (true) "계란후리아" else "사과"
    println(s"아침으로 ${breakfast}를 먹어요")
  }
}

