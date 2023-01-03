# テスト環境の構築

Docker を使って Oracle 11g のテスト環境を構築する手順です。

## リポジトリのclone

```shell
git clone https://github.com/oracle/docker-images.git
```

## Oracleイメージのダウンロード

https://www.oracle.com/database/technologies/xe-downloads.html

11gのダウンロードリンクが見つからない場合は、 [Developer Community](https://community.oracle.com/tech/developers/discussion/4490604/where-can-i-get-oracle-xe-11-2-0-1-0-x86-64-rpm-to-download) も見てみるといいと思います。

ダウンロードしたrpmのzipをcloneしたディレクトリに配置します。
ディレクトリ名は `11.0.2.2` ですが、ファイルは `11.2.0-1.0` で大丈夫です。

see: https://github.com/oracle/docker-images/blob/main/OracleDatabase/SingleInstance/dockerfiles/11.2.0.2/Dockerfile.xe#L9-L13

```
cp ~/Download/oracle-xe-11.2.0-1.0.x86_64.rpm.zip docker-images/OracleDatabase/SingleInstance/dockerfiles/11.2.0.2
```

## docker imageの作成

ビルド用のスクリプトを実行し、image を作成します。

```
cd docker-images/OracleDatabase/SingleInstance/dockerfiles
./buildContainerImage -v 11.2.0.2 -i -x
```

オプションの内訳は以下の通り

`-v 11.2.0.2`: バージョンの指定
`-i`: checksum の検証をスキップ
`-x`: Express Edition(XE) を指定


docker images で作成されたことを確認しておきます。

```
docker images
REPOSITORY        TAG          IMAGE ID       CREATED          SIZE
oracle/database   11.2.0.2-x   69bcbce2aaf6   50 minutes ago   1.15GB
```


## docker の起動

docker-compose.yml を用意しているので、それを使って起動します。

```
docker-compose up
```

## 動作確認

起動している docker に入り、 `sqlplus` で動きを確認します。

```
docker exec -it oracle11g /bin/bash
```

### テスト用ユーザの確認

`docker/script/setup/01_setup.sql` で作ったテスト用ユーザでログインできることを確認します。

```
sqlpluls scott/tiger@xe
```

### ストアドプロシージャの確認

`docker/script/setup/02_create_procedure.sql` で作ったテスト用のストアドプロシージャが実行できることを確認します。

```
set serveroutput on size 5000;
call calc.helloworld();
```

`hello world` という出力を得られれば大丈夫です。


