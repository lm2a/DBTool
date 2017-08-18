# DBTool
Tool to encode byte[] in Base64  

Nota: dbtool.jar esta compilado para java 7. Si al ejecutarlo surgiera algun error tipo Unsupported major.minor version
es que la version de java en la maquina host es inferior.
Para ejecutar en maquinas *nix simplemente dar los permisos de ejecucion a run.sh  

 sudo chmod +x run.sh  

 y ejecutarlo luego con ./run.sh  

Observese que en el directorio de distribucion hay un archivo llamado config.properties donde se pueden adaptar los
nombres de los datos (no asi lo tipos de datos)  

Por defecto este contiene los siguientes datos:  

#Generic database data  

database=mesa.s3db  
table=TFINGERPRINTS  
#path=/Users/lm2a/2017/pkg/ddbb/  

#Table fields name  
finger_b64=fingerprint  
finger_byte=fingerprintminutiae  
finger_number=finger  
record_id=id_fingerprint  
sys_date=sysdate  
voter_id=id_voter  

Observese que el path esta comentado, esto significa que debe buscar la base de datos mesa.s3db en el mismo directorio donde esta
el jar.  
Si se desea que la base de datos este en otro directorio descomentar la linea y poner el path adecuado.
Se proporcionan scripts para maquinas *nix y Windows, asi como una base de datos de nombre mesa.s3db0 que puede usarse para probar
el funcionamiento. Para ello se debe copiar en el mismo directorio como mesa.s3db. El renombrado posibilita seguir conservando la bbdd original, ya que
el proceso la invalidaria para una segunda ejecucion.  

