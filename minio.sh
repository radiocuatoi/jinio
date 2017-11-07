#!/usr/bin/env bash
if [ ! -f minio ]; then
    echo "Downloading minio"
    wget https://dl.minio.io/server/minio/release/linux-amd64/minio
fi
chmod +x minio
export MINIO_ACCESS_KEY="Q3AM3UQ867SPQQA43P2F"
export MINIO_SECRET_KEY="zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG"
./minio server --address ":19000"  $1