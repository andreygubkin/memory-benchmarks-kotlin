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
    var counter10: Long
    var counter11: Long
//  ...
    var counter12: Long
    var counter13: Long
    var counter14: Long
    var counter15: Long
    var counter16: Long
    var counter17: Long
    var counter18: Long
    var counter19: Long

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
    override var counter10: Long = 0L
    override var counter11: Long = 0L
    override var counter12: Long = 0L
    override var counter13: Long = 0L
    override var counter14: Long = 0L
    override var counter15: Long = 0L
    override var counter16: Long = 0L
    override var counter17: Long = 0L
    override var counter18: Long = 0L
    override var counter19: Long = 0L

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
        counter10 = 0L
        counter11 = 0L
        counter12 = 0L
        counter13 = 0L
        counter14 = 0L
        counter15 = 0L
        counter16 = 0L
        counter17 = 0L
        counter18 = 0L
        counter19 = 0L
    }
}
