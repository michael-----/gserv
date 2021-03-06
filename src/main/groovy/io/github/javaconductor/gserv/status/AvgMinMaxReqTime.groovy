package io.github.javaconductor.gserv.status

import io.github.javaconductor.gserv.events.Events

import javax.xml.datatype.Duration
import java.util.concurrent.atomic.AtomicLong

/**
 * Created by lcollins on 5/3/2015.
 */
class AvgMinMaxReqTime implements StatRecorder {

    def stats = [
            MaxRequestTime: new AtomicLong(0),
            MinRequestTime: new AtomicLong(0),
            AvgRequestTime: new AtomicLong(0)
    ]

    AtomicLong totalMilliseconds = new AtomicLong(0)
    AtomicLong totalRequests = new AtomicLong(0)

    def runningReqs = [:] // RegId: StartTime

    @Override
    def recordEvent(String topic, Map eventData) {

        switch (topic) {

            case Events.RequestRecieved:
                /// add to number of requests

                /// add to waiting list
                runningReqs[eventData.requestId] = eventData.when as Date
                break;
            case Events.RequestProcessingError: break;
            case Events.ResourceProcessed://
                // find the request in the eventDataByRequestId
                Date startTime = runningReqs[eventData.requestId]
                Date endTime = eventData.when
                def totalTime = endTime.time - startTime.time

                /// set the Average
                def totalMillis = totalMilliseconds.addAndGet(totalTime)
                def totalReqs = totalRequests.addAndGet(1)
                stats['AvgRequestTime'].set(totalMillis / totalReqs as long)

                /// set Min
                synchronized (stats['MinRequestTime']) {
                    if (0 == stats['MinRequestTime'].get() || totalTime < stats['MinRequestTime'].get()) {
                        stats['MinRequestTime'].set(totalTime as long)
                    }
                }

                /// set Max
                synchronized (stats['MaxRequestTime']) {
                    if (totalTime > stats['MaxRequestTime'].get()) {
                        stats['MaxRequestTime'].set(totalTime as long)
                    }
                }

                break;

        }

    }

    @Override
    Map reportStat() {
        [
                'Max Request Time': stats.MaxRequestTime.get(),
                'Min Request Time': stats.MinRequestTime.get(),
                'Avg Request Time': stats.AvgRequestTime.get()
        ]
    }

}
