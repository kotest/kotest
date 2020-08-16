Generators
==========

This page lists all current generators in Kotest. There are two types of generator - arbitrary and exhaustive.

An arbitrary will generate random values subject to its bounds (possibly with duplicates as is the nature of random selection).
An exhaustive will provide all the values over its sample space before looping if more values are required.

Most generators are available on all platforms. Some are JVM specific.

| Numeric    | Description | JVM | JS  | Native |
| -------- | ----------- | --- | --- | ------ |
| `Arb.int(range)` | Randomly chosen ints in the given range. If the range is not specified then all integers are considered. The edgecases are `Int.MIN_VALUE`, `Int.MAX_VALUE`, 0, 1, -1 | ✓ | ✓ | ✓ |
| `Arb.long(range)` | Randomly chosen longs in the given range. If the range is not specified then all longs are considered. The edgecases are `Long.MIN_VALUE`, `Long.MAX_VALUE`, 0, 1, -1 | ✓ | ✓ | ✓ |
| `Arb.nats(range)` | Randomly chosen natural numbers in the given range. If range is not specified then the default is `Int.MAX_VALUE`. The edgecases are `Int.MAX_VALUE`, 1 | ✓ | ✓ | ✓ |
| `Arb.negativeInts(range)` | Randomly chosen negative integers in the given range. The edgecases are `Int.MIN_VALUE`, -1 | ✓ | ✓ | ✓ |
| `Arb.positiveInts(range)` | Randomly chosen positive integers in the given range. The edgecases are `Int.MAX_VALUE`, 1 | ✓ | ✓ | ✓ |
| `Arb.double(range)` | Randomly chosen doubles in the given range. The edgecases are `Double.MIN_VALUE`, `Double.MAX_VALUE`, `Double.NEGATIVE_INFINITY`, `Double.NaN`, `Double.POSITIVE_INFINITY`, 0.0, 1.0, -1.0, 1e300 | ✓ | ✓ | ✓ |
| `Arb.positiveDoubles(range)` | Randomly chosen positive doubles in the given range. The edgecases are `Double.MIN_VALUE`, `Double.MAX_VALUE`, `Double.POSITIVE_INFINITY`, 1.0, 1e300 | ✓ | ✓ | ✓ |
| `Arb.negativeDoubles(range)` | Randomly chosen negative doubles in the given range. The edgecases are `Double.NEGATIVE_INFINITY`, -1.0 | ✓ | ✓ | ✓ |
| `Exhaustive.int(range)` | Returns all ints in the given range. | ✓ | ✓ | ✓ |
| `Exhaustive.long(range)` | Returns all longs in the given range. | ✓ | ✓ | ✓ |
| `Arb.multiples(k, max)` | Generates multiples of k up a max value. The edgecases are `0`. | ✓ | ✓ | ✓ |

| Booleans    | Description | JVM | JS  | Native |
| -------- | ----------- | --- | --- | ------ |
| `Exhaustive.boolean()` | Returns true and false. | ✓ | ✓ | ✓ |

| Enums    | Description | JVM | JS  | Native |
| -------- | ----------- | --- | --- | ------ |
| `Arb.enum<T>()` | Randomly selects constants from the given enum. | ✓ | ✓ | ✓ |
| `Exhaustive.enum<T>()` | Returns all the constants defined in the given enum. | ✓ | ✓ | ✓ |


| String    | Description | JVM | JS  | Native |
| -------- | ----------- | --- | --- | ------ |
| `Arb.string(range)` | Generates random printable strings with a randomly chosen size from the given range. If rangei s not specified then (0..100) is used. The edgecases include empty string, a blank string and a unicode string. | ✓ | ✓ | ✓ |
| `Exhaustive.azstring(range)` | Returns all A-Z strings in the given range. For example if range was 1..2 then a, b, c, ...., yz, zz would be included. | ✓ | ✓ | ✓ |
| `Arb.email(userRange, domainRange)` | Generates random emails where the username and domain are random strings with the size determined by the range parameters. | ✓ | ✓ | ✓ |
| `Arb.uuid(type)` | Generates random UUIDs of the given type | ✓ |  |  |

| Builders    | Description | JVM | JS  | Native |
| -------- | ----------- | --- | --- | ------ |
| `Arb.create(fn)` | Generates values using the supplied function. | ✓ | ✓ | ✓ |
| `Arb.bind(arbA, arbB, fn)` | Generates values by pulling a value from each of the two given arbs and then passing those values to the supplied function. | ✓ | ✓ | ✓ |
| `Arb.bind(arbA, arbB, arbC, fn)` | Generates values by pulling a value from each of the three given arbs and then passing those values to the supplied function. | ✓ | ✓ | ✓ |
| `Arb.bind(arbA, arbB, arbC, arbD, fn)` | Generates values by pulling a value from each of the four given arbs and then passing those values to the supplied function. | ✓ | ✓ | ✓ |
| `Arb.bind(arbA, arbB, arbC, arbD, arbE, fn)` | Generates values by pulling a value from each of the five given arbs and then passing those values to the supplied function. | ✓ | ✓ | ✓ |
| `Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, fn)` | Generates values by pulling a value from each of the six given arbs and then passing those values to the supplied function. | ✓ | ✓ | ✓ |


| Combinatorics    | Description | JVM | JS  | Native |
| -------- | ----------- | --- | --- | ------ |
| `Arb.choose(pairs)` | Generates values based on weights. For example, `Arb.choose(1 to 'A', 2 to 'B')` will generate 'A' 33% of the time and 'B' 66% of the time. | ✓ | ✓ | ✓ |
| `Arb.shuffle(list)` | Generates random permutations of a list. For example, `Arb.shuffle(listOf(1,2,3))` could generate `listOf(3,1,2)`, `listOf(1,3,2)` and so on. | ✓ | ✓ | ✓ |
| `Arb.choice(arbs)` | Randomly selects one of the given arbs and then uses that to generate the next element.  | ✓ | ✓ | ✓ |
| `Arb.subsequence(list)` | Generates a random subsequence of the given list starting at index 0 and including the empty list. For example, `Arb.subsequence(listOf(1,2,3))` could generate `listOf(1)`, `listOf(1,2)`, and so on. | ✓ | ✓ | ✓ |
| `arb.orNull()` | Generates random values from the arb instance, with null values mixed in. For example, `Arb.int().orNull()` could generate `1, -1, null, 8, 17`, and so on. Has overloaded versions to control the frequency of nulls being generated.| ✓ | ✓ | ✓ |

|  Collections  | Description | JVM | JS  | Native |
| -------- | ----------- | --- | --- | ------ |
| `Arb.list(gen, range)` | Generates lists where values are generated by the given element generator. The size of each list is determined randomly by the specified range. | ✓ | ✓ | ✓ |
| `Arb.set(gen, range)` | Generates sets where values are generated by the given element generator. The size of each set is determined randomly by the specified range. | ✓ | ✓ | ✓ |
| `Arb.element(list)` | Randomly selects one of the elements of the given list. | ✓ | ✓ | ✓ |
| `Exhaustive.collection(list)` | Enumerates each element of the list one by one. | ✓ | ✓ | ✓ |

|  Dates  | Description | JVM | JS  | Native |
| -------- | ----------- | --- | --- | ------ |
| `Arb.date(ranges)` | Generates random dates with the year between the given range |  | ✓ |  |
| `Arb.datetime(ranges)` | Generates random date times with the year between the given range |  | ✓ |  |
| `Arb.localDateTime(ranges)` | Generates random LocalDateTime's with the year between the given range | ✓ |  |  |
| `Arb.localDate(ranges)` | Generates random LocalDate's with the year between the given range | ✓ |  |  |
