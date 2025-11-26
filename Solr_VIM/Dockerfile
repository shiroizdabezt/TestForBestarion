FROM solr:6.6-alpine

COPY ./resources/*.jar /opt/solr/server/lib/

ADD https://jdbc.postgresql.org/download/postgresql-42.2.5.jar /opt/solr/contrib/extraction/lib
# Backup for case cannot download
# COPY ./resources/postgresql-42.2.5.jar /opt/solr/contrib/extraction/lib

USER root

RUN chown solr:solr /opt/solr/contrib/extraction/lib/postgresql*.jar \
    && chown solr:solr /opt/solr/server/lib/msss-transformer.jar

USER solr

