package demo.false_sharing

interface ICounters {
    var counter1: Long
    var counter2: Long
    var counter3: Long
    var counter4: Long
    var counter5: Long
    var counter6: Long
    var counter7: Long
    var counter8: Long
    var counter9: Long

    fun reset()
}

@Suppress("unused")
class Counters : ICounters {
    override var counter1: Long = 0L
    override var counter2: Long = 0L
    override var counter3: Long = 0L
    override var counter4: Long = 0L
    override var counter5: Long = 0L
    override var counter6: Long = 0L
    override var counter7: Long = 0L
    override var counter8: Long = 0L
    override var counter9: Long = 0L

    override fun reset() {
        counter1 = 0L
        counter2 = 0L
        counter3 = 0L
        counter4 = 0L
        counter5 = 0L
        counter6 = 0L
        counter7 = 0L
        counter8 = 0L
        counter9 = 0L
    }
}
