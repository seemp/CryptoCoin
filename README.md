# Hajussüsteemid P2P rakendus

Rakenduse käivitamine:  

CryptoCoin/out/artifacts/CryptoCoin_jar/  
java -jar CryptoCoin.jar 1500(port)  


Rakendus kasutab HTTP protokolli üle socketite. Toetab GET ja POST päringuid.   
Rakendus salvestab jooksvalt avastatud sõlmed Util klassis <String, Address> HashMap-i.  
Kui rakendus tööle panna lisab ta kõigepealt ennast hashmap-i ning seejärel vaatab blocks.json faili ja lisab kõik failist leitud sõlmed listi.  
Peale seda proovib ta kõikide teadaolevate sõlmedega ühendust saada(v.a iseendaga) saates neile kõigepealt POST päringu kõigi talle teadaolevate sõlmedega ning seejärel GET päringu, et saada teada neile teadaolevad sõlmed. Kui sõlm saab teada uue sõlme siis saadetakse päringud uuesti, et kõik uue info saaksid.

Peale info saamist jääb programm kuulama edasiseid käsklusi.

balance (näitab rahakoti kontojääki)
transaction recipient_public_key(String) value(int) (teeb ülekande avaliku võtme omanikule summas value)
public_key(näitab node-i avalikku võtit)
mine int(difficulty) (kaevandab bloki etteantud raskusega)
is_valid (näitab kas praegune blockchain on korras)
block_nr (prindib mitu blokki on blockchainis)
addresses (prindib välja teadaolevad aadressid)

Peale iga ülekande tegemist saadekse Transaction objekt JSON kujul kõikidele teadaolevatele aadressidele, kus see lisatakse blocki kui vastab tingimustele.
Kui blokis on kokku 3 transaktsiooni siis pannakse blokk kokku ja saadetakse edasi.

Krüpteerimiseks ja võtmete loomiseks kasutan Bouncy Castle API-t. Võtmed luuakse Elliptic Curve Digital Signature Algoritmiga. Saatmiseks teisendan byte[] array kujule, millest saab Base64 encoderiga stringi teha. Lahti kodeerides on hiljem jälle võimalik see vajadusel võtmeks teisendada.

Aadressid saadetakse JSON formaadis kujul {"MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAEf+/0qdTdPnX7K3KAarO53nhWXQojn4lci8Dn06i7gw56BGW7ZYI5qxEvK5/z2aAU":{"ip":"127.0.0.1","port":1500,"publicKey":"MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAEf+/0qdTdPnX7K3KAarO53nhWXQojn4lci8Dn06i7gw56BGW7ZYI5qxEvK5/z2aAU"}}.   
Kus on aadressi omaniku public key base64 kujul, ip ja port.

Transaktsioonid saadetakse kujul:
{
  "transactionId": "7b4b0e1250d41f6d1dac5e112f027e884f5ffa23e5d0aa101a9df3adeba64039",
  "sender": "MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAEYq2fj4RKnh7mRaF+gfI3iOue4qEg71+5uWmO+GqmYk0ybmI3lpGcMspgDSN1Jsb9",
  "recipient": "MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAEA5eqg5MfqcjpdJKjwAZIujOo6/Qy01nsSjXWqdYkkZZftsjqopYytuEyshVYxxzp",
  "value": 45.0,
  "signature": "MDYCGQCi2y9n8+0QRTja9PYqR+GVIXpu5DqWe6gCGQDgPUGbjZL/o8Qx5aiRPqCfDuQrNOqDaHw\u003d"
}

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

POST /address HTTP/1.0   
Content-Length: 77  
Content-Type: application/json  
{"MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAEf+/0qdTdPnX7K3KAarO53nhWXQojn4lci8Dn06i7gw56BGW7ZYI5qxEvK5/z2aAU":{"ip":"127.0.0.1","port":1500,"publicKey":"MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAEf+/0qdTdPnX7K3KAarO53nhWXQojn4lci8Dn06i7gw56BGW7ZYI5qxEvK5/z2aAU"}}END  

Server response:  

HTTP/1.0 200  


Blokid saadetakse laiali kujul:
![pilt](https://user-images.githubusercontent.com/32220947/82493539-cff52700-9af0-11ea-9fa4-691a1b30e249.png)
