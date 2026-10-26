const { chromium } = require('./node_modules/playwright-core');
const path = require('node:path');
(async () => {
  const browser = await chromium.launch({executablePath:'C:/Program Files/Google/Chrome/Application/chrome.exe', headless:true});
  const routes = [
    ['home','/'], ['inscription','/inscription'], ['inscription-complete','/inscription?token=preview'], ['login-candidat','/login/candidat'], ['login-professeur','/login/professeur'], ['login-scolarite','/login/scolarite'], ['mot-de-passe-oublie','/mot-de-passe-oublie'], ['reinitialiser-mot-de-passe','/reinitialiser-mot-de-passe?token=preview'],
    ['candidat-dashboard','/candidat','candidat'], ['candidat-profil','/candidat/profil','candidat'], ['candidat-diplomes','/candidat/diplomes','candidat'], ['candidat-sujets','/candidat/sujets','candidat'], ['candidat-sujet-detail','/candidat/sujets/1','candidat'], ['candidat-candidatures','/candidat/candidatures','candidat'], ['candidat-resultats','/candidat/resultats','candidat'],
    ['professeur-dashboard','/professeur','professeur'], ['professeur-sujets','/professeur/sujets','professeur'], ['professeur-candidats','/professeur/candidats','professeur'], ['professeur-resultats','/professeur/resultats','professeur'], ['professeur-inscrits','/professeur/inscrits','professeur'], ['ced','/ced','professeur'], ['labo','/labo','professeur'],
    ['scolarite-dashboard','/scolarite','scolarite'], ['scolarite-inscriptions','/scolarite/inscriptions','scolarite'], ['404','/404']
  ];
  const out = path.resolve('screenshots');
  const only = process.env.CAPTURE_ONLY ? new Set(process.env.CAPTURE_ONLY.split(',')) : null;
  for (const [name, route, portal] of routes) {
    if (only && !only.has(name)) continue;
    const context = await browser.newContext({viewport:{width:1440,height:1000}, deviceScaleFactor:1});
    if (portal) await context.addInitScript(({portal}) => { sessionStorage.setItem('edoc.refreshToken','screenshot-placeholder'); sessionStorage.setItem('edoc.portal',portal); }, {portal});
    const page = await context.newPage();
    await page.route('http://127.0.0.1:5173/api/**', async routeReq => {
      const url = new URL(routeReq.request().url());
      const p = url.pathname;
      if (p.endsWith('/token/refresh/')) return routeReq.fulfill({status:200,contentType:'application/json',body:JSON.stringify({access:'screenshot-access',refresh:'screenshot-refresh'})});
      if (p.endsWith('/get-user-info/')) return routeReq.fulfill({status:200,contentType:'application/json',body:JSON.stringify({email:'preview@example.test',nom:'Preview',prenom:'Utilisateur',pathPhoto:[],groups:[portal],phoneNumber:'',address:'',isActive:true,laboratoireDirecteurId:42})});
      if (p.endsWith('/verify-token/')) return routeReq.fulfill({status:200,contentType:'application/json',body:JSON.stringify({email:'preview@example.test',nom:'Preview',prenom:'Utilisateur'})});
      if (p.endsWith('/get-ced-sujets/')) return routeReq.fulfill({status:200,contentType:'application/json',body:JSON.stringify({count:0,next:null,previous:null,results:[],msg:null})});
      if (p.endsWith('/get-base-config/')) return routeReq.fulfill({status:200,contentType:'application/json',body:'null'});
      if (p.endsWith('/candidat-info/')) return routeReq.fulfill({status:200,contentType:'application/json',body:JSON.stringify({id:1,cne:'R123456789',pays:'Maroc',nom:'Preview',prenom:'Utilisateur',email:'preview@example.test',cin:'AB123456',nomCandidatAr:'',prenomCandidatAr:'',adresse:'Rabat',adresseAr:'',sexe:'',villeDeNaissance:'Rabat',villeDeNaissanceAr:'',ville:'Rabat',dateDeNaissance:'2000-01-01',typeDeHandiCape:'',academie:'',telCandidat:'0600000000',pathCv:null,pathPhoto:null,etatDossier:false,situation_familiale:'Célibataire',fonctionnaire:'non',diplomes:[]})});
      if (p.endsWith('/candidat-resultats/')) return routeReq.fulfill({status:200,contentType:'application/json',body:JSON.stringify({examens:[],inscriptions:[]})});
      if (p.endsWith('/sujets/1/')) return routeReq.fulfill({status:200,contentType:'application/json',body:JSON.stringify({id:1,professeur:{id:1,nom:'El Amrani',prenom:'Sara'},coDirecteur:null,titre:'Intelligence artificielle et santé',description:'Étude des méthodes d’apprentissage automatique pour accompagner la décision médicale.',formationDoctorale:{id:1,ced:1,etablissement:'Université Mohammed V',axeDeRecherche:'Sciences des données',pathImage:'',titre:'Sciences et technologies',initiale:'ST',dateAccreditation:'2024'},publier:true,valider:true})});
      return routeReq.fulfill({status:200,contentType:'application/json',body:JSON.stringify({count:0,next:null,previous:null,results:[],msg:null})});
    });
    try {
      await page.goto('http://127.0.0.1:5173'+route,{waitUntil:'networkidle',timeout:20000}).catch(()=>{});
      await page.waitForTimeout(1200);
      await page.screenshot({path:path.join(out,`${name}.png`),fullPage:true,animations:'disabled'});
      console.log(`${name}: ${page.url()}`);
    } catch (e) { console.log(`${name}: ERROR ${e.message}`); }
    await context.close();
  }
  await browser.close();
})();


