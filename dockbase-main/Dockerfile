FROM node:22-bookworm-slim

LABEL org.opencontainers.image.source="https://github.com/vevc/dockbase"

ENV INIT_SCRIPT=''

WORKDIR /app

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y --no-install-recommends \
        tini \
        supervisor \
        curl \
        ca-certificates \
        procps \
        wget \
        vim \
        git \
        net-tools \
        cron \
        unzip \
        iputils-ping \
        telnet \
        iproute2 \
    && mkdir -p /etc/supervisor/conf.d /var/log/supervisor \
    && chmod -R 777 /app /etc/supervisor/conf.d /var/log/supervisor \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

COPY supervisord.conf /etc/supervisor/supervisord.conf
COPY --chmod=755 entrypoint.sh /entrypoint.sh

ENTRYPOINT ["/usr/bin/tini", "--", "/entrypoint.sh"]
