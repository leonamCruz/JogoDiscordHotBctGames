# JogoDeRpgDiscord 🚀

Bot de economia e submundo para Discord, com mineração de BTC (de brincadeira), energia, trabalhos, crimes, loja e ranking.  

## ✨ Destaques
- ⚡ Sistema de energia em kWh para mineração
- 🪙 BTC com cotação dinâmica (sobe ao minerar, cai ao vender)
- 🛒 Loja com GPUs, ASICs e itens de suporte
- 🏆 Ranking com múltiplos critérios
- 📊 Observabilidade com Prometheus + Grafana

---

## 🧭 Comandos

### Economia
- `.energia` — paga a energia diária em kWh para minerar ⚡
- `.energia <packs>` — compra kWh extra (fica mais caro com ASIC) 🔋
- `.minerar` — minera BTC consumindo energia ⛏️
- `.venderbtc <quantidade>` — vende BTC no mercado 🪙
- `.cotacaobtc` — mostra o preço do BTC 📈
- `.loja` — lista produtos disponíveis 🛒
- `.comprar <id>` — compra um produto da loja 💸

### Trabalhos
- `.ifood` — entrega iFood 🚲
- `.uber` — corridas de Uber 🚗
- `.estoque` — trabalha no estoque 📦
- `.garçom` — garçom 🍽️
- `.pedreiro` — obras 🧱

### Crimes
- `.cc` — golpes com cartão 💳
- `.trafico` — tráfico 💊
- `.roubar @user` — rouba um jogador 🧤
- `.laranja` — conta laranja 🥕
- `.bet` — bet clandestina 🎲
- `.hackear` — hackear sistemas 💻
- `.sequestro` — alto risco, alto lucro 🚨

### Progresso e utilidade
- `.faculdade` — tenta se formar 🎓
- `.inventario` — seus itens e bônus 🎒
- `.ranking` — ranking geral 🏆
- `.perfil [@user]` — perfil do jogador 👤

### Social
- `.arrombar @user`
- `.gozar @user`

---

## 🧪 Observabilidade

Métricas expostas pelo Actuator (porta 7979) e coletadas por Prometheus + Grafana.

URLs:
- App: `http://localhost:6969`
- Actuator: `http://localhost:7979/actuator/metrics`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000` (admin/admin)

---

## 🚀 Como subir (Docker Compose)

1) Crie/edite `env.env` na raiz:
```
DATABASE_USER=hotbct
DATABASE_PASSWORD=SUASENHA
MARIADB_USER=hotbct
MARIADB_PASSWORD=SUASENHA
MARIADB_ROOT_PASSWORD=SUASENHA_ROOT
TOKEN_BOT_DISCORD=SEU_TOKEN
```

2) Suba tudo:
```
docker compose up -d --build
```

Para zerar o banco:
```
docker compose down -v
```

---

## 🧑‍💻 Rodar local (sem Docker)

1) Tenha MariaDB rodando localmente.  
2) Exporte variáveis do `env.env`:
```
export $(grep -v '^#' env.env | xargs)
SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run
```

---

## ⚙️ Balanceamento

Tudo é configurável em `application.properties` via prefixo `game.*`.  
Exemplos: cooldowns, ganhos, XP, impostos, energia e BTC.

---

