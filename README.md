# Hajussüsteemid P2P rakendus

Rakenduse käivitamine:  

CryptoCoin/out/artifacts/CryptoCoin_jar/  
java -jar CryptoCoin.jar xxxx(port)  


Rakendus kasutab HTTP protokolli üle socketite. Toetab GET ja POST päringuid.   
Rakendus salvestab jooksvalt avastatud sõlmed Util klassis <Long, Block> HashMap-i.  
Kui rakendus tööle panna lisab ta kõigepealt ennast hashmap-i ning seejärel vaatab blocks.json faili ja lisab kõik failist leitud sõlmed listi.  
Peale seda proovib ta kõikide teadaolevate sõlmedega ühendust saada(v.a iseendaga) saates neile kõigepealt POST päringu kõigi talle teadaolevate sõlmedega ning seejärel GET päringu, et saada teada neile teadaolevad sõlmed. Kui sõlm saab teada uue sõlme siis saadetakse päringud uuesti, et kõik uue info saaksid.



Näide päringutest:  

Client GET request:  

New connection accepted /127.0.0.1:54544  
GET /blocks HTTP/1.0  
Host: 127.0.0.1: 1500 
User-Agent: Simple Http Client  
Content-Type: application/json  
Accept-Language: en-US    
Connection: Close 

Server response:  

HTTP/1.0 200  
Content-Type: application/json  

{"1600":{"ip":"127.0.0.1","port":1600},"1500":{"ip":"127.0.0.1","port":1500}}END (END on lisatud sõnetöötluse   lihtsustamiseks)    

Client POST request:  

POST /blocks HTTP/1.0   
Content-Length: 77  
Content-Type: application/json  
{"1700":{"ip":"127.0.0.1","port":1700},"1500":{"ip":"127.0.0.1","port":1500}}END  

Server response:  

HTTP/1.0 200  

