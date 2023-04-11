from array import *
import re 
import time

# Estos son los grupos que nos interesan, si miras la pagina debugex ahi te tira los grupos y que numero tienen.
# Entonces hay que filtrar entre los grupos que tienen las transiciones que nos importan 
# y los grupos que tienen transiciones que no nos importan
# En cada array, el grupo que corresponde a cada array nunca se repite en las convinaciones,
# y es lo que uso para diferenciar las invariantes
FIRST_GROUPS = [1,4,6,9,12,14]
SECOND_GROUPS = [1,4,6,9,15,17]
THIRD_GROUPS = [1,4,6,18,20,23,25]
FOURTH_GROUPS = [1,4,6,18,20,26,28]
FIFTH_GROUPS = [1,30,32,35,38,40]
SIXTH_GROUPS = [1,30,32,35,41,43]
SEVENTH_GROUPS = [1,30,32,44,46,49,51]
EIGHTH_GROUPS = [1,30,32,44,46,52,54]
# FIRST_GROUPS = ["1","4","6","9","12","14"]
# SECOND_GROUPS = ["1","4","6","9","15","17"]
# THIRD_GROUPS = ["1","4","6","18","20","23","25"]
# FOURTH_GROUPS = ["1","4","6","18","20","26","28"]
# FIFTH_GROUPS = ["1","30","32","35","38","40"]
# SIXTH_GROUPS = ["1","30","32","35","41","43"]
# SEVENTH_GROUPS = ["1","30","32","44","46","49","51"]
# EIGHTH_GROUPS = ["1","30","32","44","46","52","54"]

CONVINATIONS = [
    FIRST_GROUPS,
    SECOND_GROUPS,
    THIRD_GROUPS,
    FOURTH_GROUPS,
    FIFTH_GROUPS,
    SIXTH_GROUPS,
    SEVENTH_GROUPS,
    EIGHTH_GROUPS
]

# expresion regular que encuentra las invariantes
pattern = r"(T0)(((T1)(.*?)(T3)(.*?))((T5)(.*?)((T9)(.*?)(T15)|(T10)(.*?)(T16))|(T13)(.*?)(T7)(.*?)((T9)(.*?)(T15)|(T10)(.*?)(T16)))|((T2)(.*?)(T4)(.*?))((T6)(.*?)((T11)(.*?)(T15)|(T12)(.*?)(T16))|(T14)(.*?)(T8)(.*?)((T11)(.*?)(T15)|(T12)(.*?)(T16))))"

# abrimos el archivo y pasamos todas las lineas a una variable
with open('Tlog.txt') as file:
    transitions = file.readlines()
transitions = transitions[1]
# print(transitions)


while 1:
    # print(transitions)
    # buscamos un match
    result = re.search(pattern,transitions)
    print(result)
    # si result es None significa que no se encontro match, salimos del loop
    if result == None:
        break

    # posicion del caracter donde inicia el match
    start = result.span()[0]

    # si el match no empieza en 0 entonces dividimos el string
    if start > 0:
        pre, transitions = transitions[:start], transitions[start:]
    else:
        pre = ""

    print("===============================================")
    # obtenemos un array de lo que fue matcheado por todos los grupos que conforman la regex
    groups_list = list(result.groups())
    print("Inicio de match: " + str(start))
    print("Grupos: ", end="")
    print(*groups_list, sep=", ")
    print("Match encontrado: ", end="")

    # vamos a revisar a que invariante corresponde
    # loopeamos entre todas
    for groups in CONVINATIONS:
        
        # ultima posicion del array
        last_group = groups[len(groups) - 1] - 1

        # revisamos si en el grupo final hay una transicion o no. Eso indica si fue esa la invariante encontrada
        if groups_list[last_group] != None:
            # revisamos si en el grupo final hay una transicion o no. Eso indica si fue esa la invariante encontrada
            if len(groups_list[last_group]) == 2 or len(groups_list[last_group]) == 3:
                # vamos grupo por grupo obteniendo la transicion a la que corresponde y eliminandola del string de transiciones.
                # agregue (?=\w) porque si ponia T1 por ejemplo y encontraba una T10 me sacaba el T1 y quedaba el 0 colgado.
                for group in groups:
                    print(groups_list[group-1], end="")
                    transitions = re.sub(groups_list[group-1] + r'(?=\w)',"",transitions,1)
            break

    transitions = pre + transitions
    print()
    # time.sleep(1)

print("===============================================")                
print("\nTransiciones restantes: " + transitions + "\n")


#fijarse con las transiciones restantes de dispararlas segun la red. Deberia quedar un marcado igual al que figura al final de la ejecucion del main.
#creo que se dispara desde el marcado inicial ya que se cuando quedan las restantes se completaron ciclos q hacen q el marcado quede como al principio, es decir
# si no hubiesen transiciones restantes significa que se completaron las invariantes y el marcado en teoria deberia quedar igual q el marcado inicial.
# eso recuerdo del trabajo que hicieron el grupo de fran bonino, ver eso.

# hice eso, a partir del marcado inicial fui ejecutando las transiciones que me sobraron y llegue al marcado final de la red despues de una ejecucion
# esto me sobro del script: T0T2T4T6T12
# y este es el marcado que quedo

# 0
# 0
# 0
# 8
# 7
# 4
# 4
# 0
# 0
# 0
# 1
# 1
# 1
# 1
# 0
# 0
# 1
# 0
# 0

# el tema es que hice la red en PIPE y no me tiro las transiciones en el mimsmo orden que estan puestas aca asi que es un quilombo
# pero bueno mas o menos me doy cuenta. Voy a probar con mas ejecuciones... Fijarse si en las propiedades del pipe te salen en algun 
# lugar en el mismo orden, porque de algun lado sacamos ese orden del marcado inicial
# No siempre las transiciones que sobran se pueden ejecutar, por ejemplo T0T2T11T4T6T16 sobro de una ejecucion de 357 tareas
# y partiendo desde el marcado inicial no hay chance que llegue al marcado final (mismo que esta arriba) Raro, deberia quedar otro marcado si sobraron otras transiciones, pero bueno va queriendo



# con este marcado y con estas transiciones sobrantes T0T1T0T1T3T13T7T9T3T5T10 funciono joya

 #0
 #0
 #0
 #7
 #7
 #4
 #4
 #0
 #0
 #1
 #1
 #1
 #1
 #1
 #0
 #0
 #1
 #0
 #0



 # ahora que hice los cambios de las temporales queda esto T0T2T0T1T0T2T0T1T0T1T0T2T0T2T0T1T0T4T6T2T3T5T0T1T0T11T4T2T6T10T0T3T5T1T0T12T4T6T2T9T3T0T1T5T0T10T3T13T1T11T0T7T4T14T2T0T8T9T3T1T5T12T4T14T10T8T3T13T7T11T4T14T8T12T4T9T6T3T5T10T3T5T11T4T14T8T12T9T3T5
# es como si no se ejecutaran las de vaciar y quedan todas las otras transiciones colgadas. Voy a debuggear en estos dias y ademas cambiar las matrices de incidencia, marcado y eso conr especto a la red que hice en PIPE
# sino se hace dificil

# no se si seria bueno cambiar las matrices, porque mucho esta segun ese orden inicial y quedo asi, ahi abajo puse la descripcion de cada uno para guiarse mejor.transicion

# Marcado inicial con sus plazas:

# 0 ColaP1
# 0 ColaP2
# 0 ColaProcesos
# 8 DisponibleM1
# 8 DisponibleM2
# 4 LimiteColaP1
# 4 LimiteColaP2
# 0 ListoP1
# 0 ListoP2
# 0 M1
# 0 M2
# 1 P0
# 1 Procesador1
# 1 Procesador2
# 0 ProcesandoP1
# 0 ProcesandoP2
# 1 RecursoTarea
# 0 Tarea2P1
# 0 Tarea2P2

# pareceria funcionar bien el script, probe muchas veces y siempre tira algo coherente segun la cantidad de transiciones sobrantes
# o la cantidad en memoria que no se alcanzaron a vaciar.
# El unico detalle es que cuando coincide que no sobra ninguna transicion y queda la marca inicial al final del programa
# figura como transicion restante la T15 o la T16, dependiendo cual fue la ultima q se ejecuto.
# se hizo la prueba con una sola tarea, cosa de que quedara una sola invariante de transicion completa ejecutada en el log
# y pasaba eso, que figura una restante en el script, cuando no sobra ninguna en el tlog

# si, eso pasa, queda la ultima transicion sin borrar por alguna razon. Pero al fin y al cabo funciona bien.