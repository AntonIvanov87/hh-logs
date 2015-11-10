package ru.hh.logs.intbal

private[intbal] case class IntbalLine(callerIP: String,
                                      httpMethod: String,
                                      pathAndQuery: String,
                                      cacheStatus: String,
                                      responseCode: String,
                                      duration: Float)
