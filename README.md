# Hajussüsteemid P2P rakendus

Rakenduse käivitamine:  

CryptoCoin/out/artifacts/CryptoCoin_jar/  
java -jar CryptoCoin.jar 1500(port)  


Rakendus kasutab HTTP protokolli üle socketite. Toetab GET ja POST päringuid.   
Rakendus salvestab jooksvalt avastatud sõlmed Util klassis <Long, Block> HashMap-i.  
Kui rakendus tööle panna lisab ta kõigepealt ennast hashmap-i ning seejärel vaatab blocks.json faili ja lisab kõik failist leitud sõlmed listi.  
Peale seda proovib ta kõikide teadaolevate sõlmedega ühendust saada(v.a iseendaga) saates neile kõigepealt POST päringu kõigi talle teadaolevate sõlmedega ning seejärel GET päringu, et saada teada neile teadaolevad sõlmed. Kui sõlm saab teada uue sõlme siis saadetakse päringud uuesti, et kõik uue info saaksid.

Peale info saamist jääb programm kuulama edasiseid käsklusi.

balance (prints out wallet balance)
transaction recipient_public_key(String) value(int) (makes transaction to owner of public key in sum of value)
public_key(prints out public key)
mine int(difficulty) (mines block)

Peale ülekande tegemist saadekse Transaction objekt JSON kujul kõikidele teadaolevatele aadressidele, kus see lisatakse blocki kui vastab tingimustele.

Aadressid saadetakse JSON formaadis kujul {"MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAEf+/0qdTdPnX7K3KAarO53nhWXQojn4lci8Dn06i7gw56BGW7ZYI5qxEvK5/z2aAU":{"ip":"127.0.0.1","port":1500,"publicKey":"MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAEf+/0qdTdPnX7K3KAarO53nhWXQojn4lci8Dn06i7gw56BGW7ZYI5qxEvK5/z2aAU"}}.   
Kus on aadressi omaniku public key base64 kujul, ip ja port.

Näide päringutest:  

Client GET request:  

New connection accepted /127.0.0.1:54544  
GET /address HTTP/1.0  
Host: 127.0.0.1: 1500 
User-Agent: Simple Http Client  
Content-Type: application/json  
Accept-Language: en-US    
Connection: Close 

Server response:  

HTTP/1.0 200  
Content-Type: application/json  

{"MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAEf+/0qdTdPnX7K3KAarO53nhWXQojn4lci8Dn06i7gw56BGW7ZYI5qxEvK5/z2aAU":{"ip":"127.0.0.1","port":1500,"publicKey":"MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAEf+/0qdTdPnX7K3KAarO53nhWXQojn4lci8Dn06i7gw56BGW7ZYI5qxEvK5/z2aAU"}}END (END on lisatud sõnetöötluse   lihtsustamiseks)    

Client POST request:  

POST /blocks HTTP/1.0   
Content-Length: 77  
Content-Type: application/json  
{"MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAEf+/0qdTdPnX7K3KAarO53nhWXQojn4lci8Dn06i7gw56BGW7ZYI5qxEvK5/z2aAU":{"ip":"127.0.0.1","port":1500,"publicKey":"MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAEf+/0qdTdPnX7K3KAarO53nhWXQojn4lci8Dn06i7gw56BGW7ZYI5qxEvK5/z2aAU"}}END  

Server response:  

HTTP/1.0 200  



![pilt](https://user-images.githubusercontent.com/32220947/77566611-95935300-6ece-11ea-984a-3b7b23af1e46.png)
