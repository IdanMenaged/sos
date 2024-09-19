"""
Idan Menaged
"""

import wx
from client import Client
from constants import *

# ui constants
WIDTH, HEIGHT = 300, 250
BORDER = 5
DROP_DOWN_TEXT_POS = (130, 50)
DROP_DOWN_POS = (130, 70)
SEND_BUTTON_POS = (140, 150)
N_PARAMS = 2

# other constants
IP = '127.0.0.1'


def main():
    """
    begin app loop
    create gui
    when user quits, end loop
    """
    ex = wx.App()
    GUI()
    ex.MainLoop()


class GUI(wx.Frame):
    """
    gui for client
    """
    def __init__(self):
        super().__init__(None, title='technician server', size=(WIDTH, HEIGHT))
        self.params = []
        self.combo_box = None
        self.client = None
        self.InitUI()

    def InitUI(self):
        """
        create initial ui
        """

        # create menu
        menu_bar = wx.MenuBar()
        file_menu = wx.Menu()
        menu_item = file_menu.Append(wx.ID_EXIT, 'Quit', 'Quit application')
        menu_bar.Append(file_menu, 'Menu&')
        self.SetMenuBar(menu_bar)
        self.Bind(wx.EVT_MENU, self.OnQuit, menu_item)

        # create input boxes
        pnl = wx.Panel(self)
        sb = wx.StaticBox(pnl, label='Parameters')
        sbs = wx.StaticBoxSizer(sb, orient=wx.VERTICAL)
        text_one = wx.StaticText(pnl, label='Param 1')
        text_two = wx.StaticText(pnl, label='Param 2')
        param_one = wx.TextCtrl(pnl)
        param_two = wx.TextCtrl(pnl)
        self.params.append(param_one)
        self.params.append(param_two)
        sbs.Add(text_one)
        sbs.Add(self.params[0], flag=wx.LEFT, border=BORDER)
        sbs.Add(text_two)
        sbs.Add(self.params[1], flag=wx.LEFT, border=BORDER)
        pnl.SetSizer(sbs)

        # dropdown menu
        commands = ['dir', 'take_screenshot', 'send_file', 'delete', 'copy', 'execute', 'exit', 'echo', 'history']
        wx.StaticText(pnl, pos=DROP_DOWN_TEXT_POS, label='Command')
        self.combo_box = wx.ComboBox(pnl, pos=DROP_DOWN_POS, choices=commands, style=wx.CB_READONLY)

        # send btn
        cbtn = wx.Button(pnl, label='Send', pos=SEND_BUTTON_POS)
        cbtn.Bind(wx.EVT_BUTTON, self.on_send)

        # create client
        self.client = Client(IP, PORT)

        # MUST be last
        self.Centre()
        self.Show(True)

    def OnQuit(self, e):
        """
        close window when quit button is clicked
        """
        self.Close()

    def on_send(self, e):
        """
        generate a query, send to server, and display response in a message box
        """
        # create query
        command = self.combo_box.GetStringSelection()
        params = ''
        for i in range(N_PARAMS):
            params += self.params[i].GetLineText(0)  # read line 0 of the text
            params += ' '
        query = f'{command} {params}'

        # comm with server
        res = self.client.send_command(query)
        if res == 'quit':
            wx.MessageBox('Server is down. Closing program', 'Response', wx.OK | wx.ICON_INFORMATION)
            self.Close()
        else:
            wx.MessageBox(res, 'Response', wx.OK | wx.ICON_INFORMATION)


if __name__ == '__main__':
    main()
