/**
 * Main functions
 */
//google.load("visualization", "1", {packages:["corechart"]});
var betTxt = "";
var km;
var dz;

function setBetTxt(){
	km = document.getElementById('kilometerInput').value;
	dz = document.getElementById('drehzahlInput').value;
	
	if(km != "" && dz != ""){
		betTxt = "Versuche in den nächsten " + km + " Kilometern maximal 3 Mal die Drehzahl von " + dz + " zu überschreiten!";
		localStorage.setItem("sharedTxt", betTxt);
		localStorage.setItem("sharedKM", km);
		localStorage.setItem("sharedDZ", dz);
		location.href='newBet_page2.html';
	}
	else{
		betTxt = "";
		alert("Gib bitte einen Wert für Kilometer und Drehzahl ein!");
	}
}

function setActiveTxt(){
	document.getElementById("activeBetTxt").innerHTML = localStorage.getItem("sharedTxt");
}