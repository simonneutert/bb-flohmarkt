FROM babashka/babashka:1.3.186

RUN apt update -y \
    && apt install -y \
    curl \
    cargo \
    && rm -rf /var/lib/apt/lists/*

ENV PATH="/root/.cargo/bin:${PATH}"
RUN cargo install htmlq

WORKDIR /app

COPY src /app/src
COPY bb.edn /app/

RUN mkdir .cache
CMD bb --prn scrape
