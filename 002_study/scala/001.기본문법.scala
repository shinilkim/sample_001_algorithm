package ksi.scala
import scala.collection.mutable;
import scala.collection.immutable;

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
    //    main13(args)
    //    main14(args)
    //    main15(args)
    //    main16(args)
    //    main17(args)
    //    main18(args)
    //    main19(args)
    //    main20(args)
    main21(args)
  }

  def main21(args: Array[String]): Unit = {
    // 스칼라에서는 이렇게 간단하게 만들 수 있다.
    import beans._
    class SPerson(@BeanProperty var name: String) // @BeanProper는 자바 스타일의 getter, setter가 필요한 경우만 사용
    // 자바 스타일 클래스
    class JPerson() {
      var _name: String = null
      def this(_name: String) = {
        this()
        this._name = _name
      }
      // 스칼라 스타일의 getter, setter
      def name_=(_name:String) = this._name = _name
      def name = this._name
      // 자바 스타일의 getter, setter
      def getName = name
      def setName(name: String) = this.name = name
    }
    
    val jp = new JPerson("자바 스타일")
    val sp = new SPerson("스칼라 스타일")
    println(jp.name)
    println(sp.name)
    jp.name += " 싫어요!"
    sp.name += " 좋아요!"
    println(jp.getName)
    println(sp.getName)
  }

  def main20(args: Array[String]): Unit = {
    println("============== main-20 ===================")
    // 01. 단순한 클래스
    class Person1(fname: String, lname: String)
    // 02. 메서드를 가지는 클래스
    class Person2(fname: String, lname: String) {
      def greet = s"${lname}${fname}님 안녕하세요!"
    }
    // 03. public한 val(read only) 필드
    class Person3(fname: String, lname: String) {
      val fullName = s"${lname}${fname}" // read only
      def greet = s"${fullName}님 안녕하세요!"
    }
    // 04. val로 선언된 fname는 getter가 자동으로 생성(var로 선언된 lname은 getter & setter 자동으로 생성)
    class Person4(val fname: String, var lname: String)
    println("== 01. 단순한 클래스")
    val p1 = new Person1("중기", "송")
    println("p1.fname, p1.lname은 값을 외부에서 가져올 수 없다")

    println("== 02. 메소드를 가지는 클래스")
    val p2 = new Person2("혜교", "송")
    println(s"Person2: ${p2.greet}")

    println("== 03. public한 read only(val) fullname을 가지는 클래스")
    val p3 = new Person3("구", "진")
    println(s"${p3.fullName}님께 인사합니다. ${p3.greet}")

    println("== 04. val fname과 lname을 가지는 클래스")
    val p4 = new Person4("지원", "Kim") {
      override def toString = s"$lname$fname"
    }
    println(s"${p4.lname}${p4.fname}")

    println("== 05. Person4 클래스를 이용해서 객체를 생성하지만, 해당 객체의 toString메소드만 오버라이드")
    val p5 = new Person4("시진", "유") {
      override def toString = s"$lname$fname"
    }
    println(s"$p5")
  }

  def main19(args: Array[String]): Unit = {
    println("============== main-19 ===================")
    println("== 01. 변경할 수 없는 Collection이 var로 선언된 경우")
    var immutableSet = Set(1, 2, 3)
    immutableSet += 4 // 새로운 Set을 만들어서 immutableSet에 저장.(var 이 아니라 val 이면 오류)
    println(s"immutableSet: $immutableSet")
    println("== 02. 변경할 수 있는 Collection 이라면 추가하는 Method를 호출하는것과 같음")
    val mutableSet = mutable.Set(1, 2, 3)
    mutableSet += 4
    mutableSet.+=(4)
    println(s"mutableSet: $mutableSet")
  }

  def main18(args: Array[String]): Unit = {
    println("============== main-18 ===================")
    println("== 01. 배열로 구현되는 ArrayBuffer")
    val arrayBuffer = mutable.ArrayBuffer(1, 2, 3)
    arrayBuffer += 4
    arrayBuffer -= 1
    arrayBuffer ++= List(5, 6, 7)
    println(s"arrayBuffer: ${arrayBuffer}")
    println("== 02. Linked list로 구현되는 ListBuffer")
    val listBuffer = mutable.ListBuffer("a", "b", "c")
    println(s"listBuffer: $listBuffer")
    println("== 03. Mutable Set")
    val hashSet = mutable.Set(0.1, 0.2, 0.3)
    hashSet ++= mutable.Set(5)
    println(s"hashSet: ${hashSet}")
    println("== 04. Mutable Map")
    val hashMap = mutable.Map("one" -> 1, "two" -> 2)
    hashMap ++= Map("five" -> 5, "six" -> 6)
    println(s"hashMap: $hashMap")
  }

  def main17(args: Array[String]): Unit = {
    println("============== main-17 ===================")
    class Animal()
    class Dog() extends Animal()
    val array: Array[Animal] = Array(new Animal(), new Dog())
    val list: List[Animal] = List(new Animal(), new Dog())
    val set: Set[Animal] = Set(new Animal(), new Dog())
    val map: Map[String, Animal] = Map("Animal" -> new Animal(), "Dog" -> new Dog())
  }

  def main16(args: Array[String]): Unit = {
    println("============== main-16 ===================")
    println("== 01. Map[String,Int] 타입의 맵")
    val map1 = Map("one" -> 1, "two" -> 2, "three" -> 3)
    val map2 = Map(1 -> "one", "2" -> 2.0, "three" -> false) // Map[Any, Any] 타입의 맵
    println(s"map1: $map1, map2: $map2")
    println(s"중복된 키가 있으면 마지막 값을 사용: ${Map('a' -> 1, 'a' -> 2)}")
    println(s"key를 가지고 값을 읽어오기: ${map1("one")} ${map1.get("two1")}") // map1("two1") 하면 NoSuchElementException 발생
    val personMap = Map(("솔라", 1), ("문별", 2), ("휘인", 3))
    def findByName(name: String) = personMap.getOrElse(name, 4)
    val findSolar = findByName("솔라")
    val findSun = findByName("태양")
    println(s"findSolar: $findSolar, findSun: $findSun")
  }

  def main15(args: Array[String]): Unit = {
    println("============== main-15 ===================")
    println("== 01. 내용을 수정할 수 없는 Set")
    val set1 = Set("One", 1)
    val set2 = Set(1, 2, 2, 2, 3, 3, 3, 4) // 중복이 제거 된다.
    println(s"set1: $set1")
    println(s"set2: $set2")
    println("== 02. 값이 있는지 체크하는 방법은 괄호 안에 값을 넣어서 사용")
    val oneExists = set2(1)
    val fourExists = set2(2)
    println(s"oneExists: ${oneExists}, fourExists: ${fourExists}")
    println("== 03. set을 더하면 중복된 내용은 제거된 새로운 Set이 생성")
    val concatenated = set1 ++ set2
    println(s"concatenated: ${concatenated}")
    println("== 04. Diff")
    val diffSet = Set(1, 2, 3, 4) diff Set(2, 3)
    println(s"diffSet: ${diffSet}")
    println("== 05. 검색")
    val personSet = Set(("솔라", 1), ("문별", 2), (" 휘인", 3))
    def findByName(name: String) = personSet.find(_._1 == name).getOrElse(("회사", 4))
    val findSolar = findByName("솔라")
    val findSun = findByName("태양")
    println(s"findSolar: ${findSolar}, findSun: $findSun")
  }

  def main14(args: Array[String]): Unit = {
    println("============== main-14 ===================")
    val list = List("a", 1, true)
    val firstItem = list(0)
    println(s"firstItem: $firstItem")

    val concatenated = 0 :: list ++ list :+ 1000
    println(s"concatenated: $concatenated")

    val diffList = List(1, 2, 3, 4) diff List(2, 3)
    println(s"diffList: $diffList")

    val personList = List(("솔라", 1), ("문별", 2), ("휘인", 3))
    def findByName(name: String) = personList.find(_._1 == name).getOrElse(("회사", 4))
    val findSolar = findByName("솔라")
    val findSun = findByName("태양")
    println(s"findSolar: $findSolar, findSun: $findSun")
  }

  def main13(args: Array[String]): Unit = {
    println("============== main-13 ===================")
    // 배열의 내용을 출력하는 메소드
    def printArray[K](array: Array[K]) = println(array.mkString("Array(", ", ", ")"))
    val array1 = Array(1, 2, 3)
    printArray(array1)

    val array2 = Array("a", 2, true)
    printArray(array2)

    val itemAtIndex0 = array1(0)
    array1(0) = 4
    printArray(array1)
    println(s"mkString: ${array1.mkString(",")}")

    val concatenated = "앞에 붙이기" +: (array1 ++ array2) :+ "뒤에 붙이기"
    print("array1과 array2를 더하면:")
    printArray(concatenated)
    println(array2.indexOf("a"))

    val diffArray = Array(1, 2, 3, 4).diff(Array(2, 3))
    printArray(diffArray)

    val personArray = Array(("솔라", 1), ("문별", 2), ("휘인", 3))
    def findByName(name: String) = personArray.find(_._1 == name).getOrElse(("회사", 4))
    val findSolar = findByName("솔라")
    val findSun = findByName("태양")
    println(findSolar)
    println(findSun)
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

  def main9(args: Array[String]): Unit = {
    println("============== main-09 ===================")
    var (x, y, z, c, python, java) = (1, 2, 3, true, false, "no!")
    println(x, y, z, c, python, java)
    println(c)
  }

  def main8(args: Array[String]): Unit = {
    println("============== main-08 ===================")
    def swap(x: String, y: String) = (y, x)
    val (a, b) = swap("hello", "world")
    println(a, b)
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
