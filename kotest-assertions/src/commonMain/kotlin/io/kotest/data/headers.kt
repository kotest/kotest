package io.kotest.data

fun headers(a: String) = Headers1(a)
fun headers(a: String, b: String) = Headers2(a, b)
fun headers(a: String, b: String, c: String) = Headers3(a, b, c)
fun headers(a: String, b: String, c: String, d: String) = Headers4(a, b, c, d)
fun headers(a: String, b: String, c: String, d: String, e: String) = Headers5(a, b, c, d, e)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String) = Headers6(a, b, c, d, e, f)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String) = Headers7(a, b, c, d, e, f, g)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String) = Headers8(a, b, c, d, e, f, g, h)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String) = Headers9(a, b, c, d, e, f, g, h, i)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String) = Headers10(a, b, c, d, e, f, g, h, i, j)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String) = Headers11(a, b, c, d, e, f, g, h, i, j, k)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String, l: String) = Headers12(a, b, c, d, e, f, g, h, i, j, k, l)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String, l: String, m: String) = Headers13(a, b, c, d, e, f, g, h, i, j, k, l, m)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String, l: String, m: String, n: String) = Headers14(a, b, c, d, e, f, g, h, i, j, k, l, m, n)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String, l: String, m: String, n: String, o: String) = Headers15(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String, l: String, m: String, n: String, o: String, p: String) = Headers16(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String, l: String, m: String, n: String, o: String, p: String, q: String) = Headers17(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String, l: String, m: String, n: String, o: String, p: String, q: String, r: String) = Headers18(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String, l: String, m: String, n: String, o: String, p: String, q: String, r: String, s: String) = Headers19(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String, l: String, m: String, n: String, o: String, p: String, q: String, r: String, s: String, t: String) = Headers20(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String, l: String, m: String, n: String, o: String, p: String, q: String, r: String, s: String, t: String, u: String) = Headers21(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)
fun headers(a: String, b: String, c: String, d: String, e: String, f: String, g: String, h: String, i: String, j: String, k: String, l: String, m: String, n: String, o: String, p: String, q: String, r: String, s: String, t: String, u: String, v: String) = Headers22(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)

data class Headers1(val labelA: String) {
   fun values() = listOf(labelA)
}

data class Headers2(val labelA: String, val labelB: String) {
   fun values() = listOf(labelA, labelB)
}

data class Headers3(val labelA: String, val labelB: String, val labelC: String) {
   fun values() = listOf(labelA, labelB, labelC)
}

data class Headers4(val labelA: String, val labelB: String, val labelC: String, val labelD: String) {
   fun values() = listOf(labelA, labelB, labelC, labelD)
}

data class Headers5(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String) {
   fun values() = listOf(labelA, labelB, labelC, labelD, labelE)
}

data class Headers6(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String) {
   fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF)
}

data class Headers7(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String) {
   fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG)
}

data class Headers8(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String) {
   fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH)
}

data class Headers9(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String) {
   fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI)
}

data class Headers10(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String) {
   fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ)
}

data class Headers11(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String) {
   fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK)
}

data class Headers12(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String, val labelL: String) {
   fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK, labelL)
}

data class Headers13(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String, val labelL: String, val labelM: String) {
   fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK, labelL, labelM)
}

data class Headers14(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String, val labelL: String, val labelM: String, val labelN: String) {
   fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK, labelL, labelM, labelN)
}

data class Headers15(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String, val labelL: String, val labelM: String, val labelN: String, val labelO: String) {
   fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK, labelL, labelM, labelN, labelO)
}

data class Headers16(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String, val labelL: String, val labelM: String, val labelN: String, val labelO: String, val labelP: String) {
   fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK, labelL, labelM, labelN, labelO, labelP)
}

data class Headers17(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String, val labelL: String, val labelM: String, val labelN: String, val labelO: String, val labelP: String, val labelQ: String) {
   fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK, labelL, labelM, labelN, labelO, labelP, labelQ)
}

data class Headers18(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String, val labelL: String, val labelM: String, val labelN: String, val labelO: String, val labelP: String, val labelQ: String, val labelR: String) {
   fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK, labelL, labelM, labelN, labelO, labelP, labelQ, labelR)
}

data class Headers19(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String, val labelL: String, val labelM: String, val labelN: String, val labelO: String, val labelP: String, val labelQ: String, val labelR: String, val labelS: String) {
   fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK, labelL, labelM, labelN, labelO, labelP, labelQ, labelR, labelS)
}

data class Headers20(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String, val labelL: String, val labelM: String, val labelN: String, val labelO: String, val labelP: String, val labelQ: String, val labelR: String, val labelS: String, val labelT: String) {
   fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK, labelL, labelM, labelN, labelO, labelP, labelQ, labelR, labelS, labelT)
}

data class Headers21(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String, val labelL: String, val labelM: String, val labelN: String, val labelO: String, val labelP: String, val labelQ: String, val labelR: String, val labelS: String, val labelT: String, val labelU: String) {
   fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK, labelL, labelM, labelN, labelO, labelP, labelQ, labelR, labelS, labelT, labelU)
}

data class Headers22(val labelA: String, val labelB: String, val labelC: String, val labelD: String, val labelE: String, val labelF: String, val labelG: String, val labelH: String, val labelI: String, val labelJ: String, val labelK: String, val labelL: String, val labelM: String, val labelN: String, val labelO: String, val labelP: String, val labelQ: String, val labelR: String, val labelS: String, val labelT: String, val labelU: String, val labelV: String) {
   fun values() = listOf(labelA, labelB, labelC, labelD, labelE, labelF, labelG, labelH, labelI, labelJ, labelK, labelL, labelM, labelN, labelO, labelP, labelQ, labelR, labelS, labelT, labelU, labelV)
}
