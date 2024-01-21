create bankmas-report-system
mvn archetype:generate -DgroupId=com.bankmas.report -DartifactId=bankmas-report-system -DinteractiveMode=false

cd bankmas-report-system
mvn archetype:generate -DgroupId=com.bankmas.report.servicepdf -DartifactId=servicepdf -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
mvn archetype:generate -DgroupId=com.bankmas.report.servicecsv -DartifactId=servicecsv -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
mvn archetype:generate -DgroupId=com.bankmas.report.serviceexcel -DartifactId=serviceexcel -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
mvn archetype:generate -DgroupId=com.bankmas.report.webapi -DartifactId=webapi -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false

3086: remote -> org.springframework.boot:spring-boot-sample-data-rest-archetype (Spring Boot Data REST Sample)

test build
mvn clean install

debug app
mvn spring-boot:run

create jar package
mvn package spring-boot:repackage
mvn package

java -jar target/java-template-0.0.1-SNAPSHOT.jar

tugas ==>

web api
1. menerima file data berupa json dengan format yg sudah di tentukan
   - menyimpan file tersebut di dalam repository file untuk siap di proses
   - mencatat file tersebut di dalam database, berupa "nama file", "jenis report", "status proses", "tipe report" (pdf/csv/excel), "tanggal proses", "tanggal selesai proses" dan "checksum file" ("checksum file" digunakan untuk mengecek apakah ada file yg sama yg sedang di proses atau tidak)
   - mengirimkan event melalui kafka ke service file berdasarkan tipe extension file yg di inginkan (pdf/csv/excel) untuk memproses file tersebut
2. mendapatkan list file di dalam database
   - list berupa paging, dengan batas yg bisa di sesuikan (10, 25, 50, 100)
   - default sort dari "tanggal proses" terbaru ke terlama
   - dapat di filter berdasarkan status proses (belum di proses, sedang di proses, selesai, error)
3. mendapatkan detail file yg sedang di proses di dalam database
4. mendownload file yang sudah selesai di proses
5. menerima event berupa status proses file (selesai/error) dari service file
6. menghapus file di dalam database dan repository
7. mendapatkan list dari jenis report yang tersedia ("nama jenis report", "template file", "json data field")
8. melihat detail dari jenis report
9. menambahkan jenis report baru
10. melakukan edit jenis report
11. data "jenis report" di simpan dalam cache
    - cache di simpan dalam bentuk list
    - cache di refresh ketika ada perubahan "jenis report"
    - cache tidak ada timeout

service file (pembuatan file pdf/csv/excel)
1. menerima event dari web api untuk melaksanakan proses file
   - membaca cache "jenis report" untuk mendapatkan tipe report, template file dan json data field
   - membaca file data yg perlu di proses, apakah sesuai dengan tipe report yg di inginkan
   - membaca template file yg perlu di proses
   - melakukan proses file
   - setiap urutan proses akan mengirimkan event berupa status proses file ke web api
   - menyimpan hasil proses berupa file pdf/csv/excel ke dalam repository file
2. mengirimkan event berupa hasil proses file ke web api
3. setiap tipe report akan memiliki service yg berbeda

contoh file json => 
data.json

report output yg di harapkan =>
pada pdf file diharapkan output gambar adalah berupa "image" bukan "link"
pada excel diharapkan bisa berupa "image" dan bukan "link"