import streamlit as st
import pandas as pd
import os
import jsonlines
import plotly.express as px
import time


def load_data():
    data_path = "C:/Users/koman/Downloads/Projet annuel/Real/temp/velib_data/"
    
    files = [f for f in os.listdir(data_path) if f.endswith('.json')]
    
    if files:
        latest_file = max(files, key=lambda x: os.path.getctime(os.path.join(data_path, x)))
        print(latest_file)
        
        # Charger le fichier JSON Lines
        with jsonlines.open(os.path.join(data_path, latest_file)) as reader:
            return [obj for obj in reader]
    
    return []

st.set_page_config(page_title="Velib Real-Time Dashboard", layout="wide")

st.title("🚴‍♂️ Velib Real-Time Dashboard")
st.markdown("Visualisation des données en temps réel des stations de vélos Velib")




def get_data():
    return load_data()

data = get_data()

if data:

    df = pd.json_normalize(data)
    df = df[df['is_installed'] != 'NON']

    df = df.rename(columns={
        'numbikesavailable': 'Nombre de vélos disponibles',
        'name': 'Nom de la station',
        'numdocksavailable': 'Nombre de places disponibles',
        'mechanical': 'Nombre de vélos mécaniques disponibles',
        'ebike': 'Nombre de vélos électriques disponibles',  
        'nom_arrondissement_communes': 'Villes', 
    })

    columns_to_display = ['Nom de la station', 'Nombre de places disponibles',
                          'Nombre de vélos disponibles', 'Nombre de vélos mécaniques disponibles', 
                          'Nombre de vélos électriques disponibles', 'Villes']

    st.dataframe(df[columns_to_display])

    if 'latitude' in df.columns and 'longitude' in df.columns:
        fig = px.scatter_mapbox(
            df,
            lat="latitude",
            lon="longitude",
            color="Nombre de vélos disponibles",
            hover_name="Nom de la station", 
            hover_data=["Nombre de vélos disponibles", "Nombre de places disponibles", 'Nombre de vélos mécaniques disponibles', 
                          'Nombre de vélos électriques disponibles', "Villes"],  
            color_continuous_scale="Viridis", 
            size="Nombre de vélos disponibles",  
            size_max=20,  
            title="Disponibilité des vélos Velib par station",
            zoom=10,  
            height=600
        )
        fig.update_layout(mapbox_style="open-street-map") 
        st.plotly_chart(fig, use_container_width=True)
    else:
        st.error("Les données de latitude et longitude sont manquantes.")
else:
    st.write("Aucune donnée disponible.")

refresh_interval = 20  # Intervalle en secondes
st.markdown(f"Les données sont actualisées toutes les {refresh_interval} secondes.")

# Pause pour l'actualisation
time.sleep(refresh_interval)
st.rerun()
