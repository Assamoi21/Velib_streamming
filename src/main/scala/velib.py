import os

import jsonlines
import pandas as pd
import plotly.express as px
import streamlit as st


DATA_PATH = os.getenv("VELIB_DATA_DIR", "C:/Users/koman/Downloads/Projet annuel/Real/temp/velib_data")


@st.cache_data(ttl=30)
def load_data(path: str = DATA_PATH):
    if not os.path.exists(path):
        return []

    files = [f for f in os.listdir(path) if f.endswith('.json')]
    if not files:
        return []

    latest_file = max(files, key=lambda x: os.path.getctime(os.path.join(path, x)))
    with jsonlines.open(os.path.join(path, latest_file)) as reader:
        return [obj for obj in reader]


st.set_page_config(page_title="Velib Real-Time Dashboard", layout="wide")

st.title("🚴‍♂️ Velib Real-Time Dashboard")
st.markdown("Visualisation des données en temps réel des stations de vélos Velib")

if st.button("Actualiser les données", use_container_width=True):
    st.cache_data.clear()

raw_data = load_data()

if raw_data:
    df = pd.json_normalize(raw_data)
    df = df[df.get('is_installed') != 'NON'].copy()

    df['total_bikes'] = df.get('numbikesavailable', 0).fillna(0).astype(int) + df.get('mechanical', 0).fillna(0).astype(int) + df.get('ebike', 0).fillna(0).astype(int)

    df = df.rename(columns={
        'numbikesavailable': 'Vélos disponibles',
        'name': 'Nom de la station',
        'numdocksavailable': 'Places disponibles',
        'mechanical': 'Vélos mécaniques',
        'ebike': 'Vélos électriques',
        'nom_arrondissement_communes': 'Arrondissement',
        'capacity': 'Capacité totale',
        'stationcode': 'Code station',
    })

    cities = sorted(df['Arrondissement'].dropna().astype(str).unique().tolist())
    selected_city = st.selectbox("Filtrer par arrondissement", ["Tous"] + cities)
    if selected_city != "Tous":
        df = df[df['Arrondissement'] == selected_city]

    col1, col2, col3, col4 = st.columns(4)
    col1.metric("Stations actives", int(df.shape[0]))
    col2.metric("Vélos disponibles", int(df['Vélos disponibles'].sum()))
    col3.metric("Places disponibles", int(df['Places disponibles'].sum()))
    col4.metric("Capacité totale", int(df['Capacité totale'].sum()))

    top_stations = df.nlargest(10, 'Vélos disponibles')[['Nom de la station', 'Vélos disponibles', 'Vélos mécaniques', 'Vélos électriques', 'Places disponibles', 'Arrondissement']]
    st.subheader("Top 10 des stations les plus fournies")
    st.dataframe(top_stations, use_container_width=True)

    columns_to_display = [
        'Nom de la station', 'Code station', 'Vélos disponibles', 'Places disponibles',
        'Vélos mécaniques', 'Vélos électriques', 'Arrondissement'
    ]
    st.subheader("Données détaillées")
    st.dataframe(df[columns_to_display], use_container_width=True)

    if {'latitude', 'longitude'}.issubset(df.columns):
        fig = px.scatter_mapbox(
            df,
            lat='latitude',
            lon='longitude',
            color='Vélos disponibles',
            size='Vélos disponibles',
            hover_name='Nom de la station',
            hover_data=['Vélos disponibles', 'Places disponibles', 'Vélos mécaniques', 'Vélos électriques', 'Arrondissement'],
            color_continuous_scale='Viridis',
            size_max=25,
            title='Disponibilité des vélos Velib par station',
            zoom=10,
            height=650,
        )
        fig.update_layout(mapbox_style='open-street-map')
        st.plotly_chart(fig, use_container_width=True)
    else:
        st.warning("Les colonnes latitude/longitude ne sont pas présentes dans les données chargées.")
else:
    st.info("Aucune donnée disponible. Vérifiez que le flux Scala a bien écrit des fichiers JSON dans le dossier configuré.")
