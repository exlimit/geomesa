Advanced Topics
===============

Parallelism
-----------

There are two types of parallelism for the Lambda store - the number of simultaneous writers for long-term
persistence, and the number of Kafka consumers per data store.

The number of writers is determined by the number of data stores instances with a valid ``expiry`` parameter;
however the parallelism on writes is limited by how many partitions are in the Kafka topic (writes are
synchronized across instances by topic and partition). The ``kafka.partitions`` parameter can be used to control
the number of partitions, but note that once a topic is created, you will need to use Kafka scripts (i.e.
``kafka-topics.sh``) to modify partitions.

The number of consumers used to load features into the in-memory cache is controlled by the ``kafka.consumers``
parameter. This is the number of consumers per data store instance per simple feature type accessed. Note that
having more consumers than topic partitions is not recommended and will cause some consumers to be idle.

Installation Tips
-----------------

The typical use case for a Lambda data store is to be fed by an analytic streaming process. The analytic will
update summary ``SimpleFeature``\ s with derived attributes containing analytic results. For example, when
aggregating GPS tracks, there could be a dynamically updated field for current estimated heading.

Typical streaming analytics will run in many concurrent threads, for example using `Apache Storm`_. The Lambda
store achieves parallelism based on the ``kafka.partitions`` parameter. Generally you should start with this
set to the number of writer threads being used, and adjust up or down as needed.

.. _Apache Storm: http://storm.apache.org/


Any data store instances used only for reads (e.g. GeoServer) should generally disable writing by setting the
``expiry`` parameter to ``Inf``. Note that there must be at least one data store instance with a valid ``expiry``,
or features will never be persisted to long-term storage and removed from memory.

Manual Persistence
------------------

Instead of allowing features to be persisted to long-term storage automatically, you may instead control exactly
when features are written. To do this, set the ``persist`` parameter to ``false`` and the ``expiry`` parameter to
``Inf``. Write and remove features from the Lambda data store using ``SimpleFeatureWriter``\ s as usual to
add and delete them from the in-memory cache. With ``persist`` disabled, this will not affect long-term storage.

To write features to long-term storage, instantiate an instance of the delegate data store (``AccumuloDataStore``)
using the same connection parameters as for the Lambda store. Any features written to the delegate store will
then be queryable by the Lambda store, and merged with the in-memory cache.
