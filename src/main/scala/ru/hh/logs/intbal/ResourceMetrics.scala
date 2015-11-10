package ru.hh.logs.intbal

import ru.hh.logs.Resource

case class ResourceMetrics(resource: Resource,
                           countPercent: Float,
                           totalDurationPercent: Float,
                           avgDuration: Float,
                           hitPercent: Float)
