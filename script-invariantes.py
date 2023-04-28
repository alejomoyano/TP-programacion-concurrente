from array import *
import re 

matchs_counter = 0

refill_groups = '\g<2>\g<6>\g<8>\g<11>\g<14>\g<17>\g<20>\g<22>\g<25>\g<28>\g<32>\g<34>\g<37>\g<40>\g<43>\g<46>\g<48>\g<51>\g<54>'
pattern = '(T0-)(.*?)(((T1-)(.*?)(T3-)(.*?))((T5-)(.*?)((T9-)(.*?)(T15-)|(T10-)(.*?)(T16-))|(T13-)(.*?)(T7-)(.*?)((T9-)(.*?)(T15-)|(T10-)(.*?)(T16-)))|((T2-)(.*?)(T4-)(.*?))((T6-)(.*?)((T11-)(.*?)(T15-)|(T12-)(.*?)(T16-))|(T14-)(.*?)(T8-)(.*?)((T11-)(.*?)(T15-)|(T12-)(.*?)(T16-))))'


with open('logs/Tlog.txt') as file:
    transitions = file.readlines()
transitions = transitions[1]

# reemplaza los matchs con los grupos que no contienen transiciones que correspondan a una invariante
transitions = re.subn(pattern,refill_groups,transitions)
matchs_counter += transitions[1]

while transitions[1] != 0:
    transitions = re.subn(pattern,refill_groups,transitions[0])
    print("Restante:", transitions)
    matchs_counter += transitions[1]


print("\n===============================================")
print("Transiciones restantes: " + transitions[0][:len(transitions[0])-1])
print("Cantidad de matchs encontrados: " + str(matchs_counter))
print("===============================================")


# te explico aca lo de los tiempos

# La idea es tener un tiempo aprox de cuanto tardaria en ejecutarse todo de forma secuencial y cuanto tiempo si fuera perfectamente paralelo.
# Por ejemplo, si arrivalRate se ejecuta 1000 veces, multiplicar 1000 * alfa de la transicion. Hacer esto con todas las otras temporales, sumarlo y eso seria el tiempo en secuencial.
# Despues para el caso paralelo no se si le entendi, pero deberiamos tomar la que mas tiempo gasta me parece.
# Con eso, ejecutar la red y ver en que extremo esta. Deberia estar mas cerca de la ejecucion en paralelo.
# Tambien ir variando los alfas y ver si la politica cambia y si lo hace explicar porque.